// IOnNewBookArrivedListener.aidl
package com.hy.presentation;

import com.hy.presentation.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
