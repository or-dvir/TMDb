package com.example.ordvir.tmdb.other

class TMDbCallback<T>
{
    var onSuccess: retroSuccess<T> = null

    /**
     * explicitly handle server error or null server response body
     */
    var onErrorCodeOrNullBody: retroErrorCode<T> = null

    /**
     * explicitly handle network exception
     */
    var onException: retroException<T> = null

    /**
     * convenience if handling of null body/error/exception is the same
     */
    var onErrorOrExceptionOrNullBody: retroErrorOrException<T> = null
}