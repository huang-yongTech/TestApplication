// IBookManager.aidl
package com.hy.presentation;

import com.hy.presentation.Book;
import com.hy.presentation.IOnNewBookArrivedListener;
// Declare any non-default types here with import statements

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

    void registerListener(IOnNewBookArrivedListener listener);

    void unRegisterListener(IOnNewBookArrivedListener listener);
}
