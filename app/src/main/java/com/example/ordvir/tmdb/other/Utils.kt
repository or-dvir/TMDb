package com.example.ordvir.tmdb.other

import android.app.AlertDialog
import android.app.ProgressDialog
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.ordvir.tmdb.R
import com.example.ordvir.tmdb.other.interfaces_and_enums.IDialogButtonClickedListener
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.Call

object Const
{
    const val HTTP_CODE_200_OK = 200
}

typealias retroSuccess<T> = ((originalCall: Call<T>, result: T, requestCode: Int) -> Unit)?
typealias retroErrorCode<T> = ((originalCall: Call<T>, serverErrorCode: Int, requestCode: Int) -> Unit)?
typealias retroException<T> = ((originalCall: Call<T>, exception: Exception, requestCode: Int) -> Unit)?
typealias retroErrorOrException<T> = ((originalCall: Call<T>, serverErrorCode: Int?, exception: Exception?, requestCode: Int) -> Unit)?

fun getTMDbClient() = SMyRetrofit.tmdbClient

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
fun AppCompatActivity.createAndShowSimpleDialog(@StringRes message: Int,
                                                @StringRes title: Int = -1,
                                                @StringRes btnText: Int = R.string.ok,
                                                btnListener: IDialogButtonClickedListener? = null)
{
    AlertDialog.Builder(this).apply {
        if (title != -1) setTitle(title)
        setMessage(message)
        setCancelable(false)
        setPositiveButton(btnText) { dialog, which ->
            dialog.dismiss()
            btnListener?.onDialogButtonClicked()
        }

        show()
    }
}

fun AppCompatActivity.setHomeUpEnabled(enabled: Boolean) = supportActionBar?.setDisplayHomeAsUpEnabled(enabled)

fun getUrlImage(path: String?): String
{
    //random string so picasso will show error image
    val default = "SDfdssdfsd"
    val base = SConfiguration.getBaseUrl()

    return if (path == null || base == null)
        default
    //for simplicity of this exercise, always use original image size.
    //if this was a production app, we should choose the size closest to our ImageViews'
    //aspect ratio
    else
        "$base/original/$path"
}

fun AppCompatActivity.createAndShowLoadingDialog(@StringRes message: Int = R.string.loading)
        : ProgressDialog
{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(getString(message))
        show()
    }
}

fun <T> tmdbRequestAsync(logTag: String,
                         call: Call<T>,
                         callback: TMDbCallback<T>,
                         requestCode: Int = -1)
        : Job
{
    return tmdbRequestAsync(logTag,
                            call,
                            callback.onSuccess,
                            callback.onErrorCodeOrNullBody,
                            callback.onException,
                            callback.onErrorOrExceptionOrNullBody,
                            requestCode)
}

private fun <T> tmdbRequestAsync(logTag: String,
                                 call: Call<T>,
                                 onSuccess: retroSuccess<T>,
                                 onErrorCodeOrNullBody: retroErrorCode<T>,
                                 onException: retroException<T>,
                                 onErrorOrExceptionOrNullBody: retroErrorOrException<T>,
                                 requestCode: Int)
        : Job
{
    return launch(UI)
    {
        try
        {
            val response = withContext(CommonPool) { call.execute() }
            val code = response.code()
            val body = response.body()
            var errorMessage = ""

            //NOTE:
            //response.isSuccessful() returns true for all values between 200 and 300!!!
            if(code == Const.HTTP_CODE_200_OK)
            {
                if(body != null)
                    onSuccess?.let { it(call, body, requestCode) }
                else
                    errorMessage = "server response body was NULL"

            }

            //server error code (NOT 200-ok)
            else
                errorMessage = response.message()


            if(errorMessage.isNotEmpty())
            {
                Log.e(logTag, "server error:\n" +
                        "return code: $code\n" +
                        "error message: $errorMessage\n" +
                        "original call was: ${call.request()}")

                onErrorCodeOrNullBody?.let { it(call, code, requestCode) }
                onErrorOrExceptionOrNullBody?.let { it(call, code, null, requestCode) }
            }
        }
        catch (e: Exception)
        {
            Log.e(logTag, "${e.message}\noriginal call was: ${call.request()}", e)

            onException?.let { it(call, e, requestCode) }
            onErrorOrExceptionOrNullBody?.let { it(call, null, e, requestCode) }
        }
    }
}