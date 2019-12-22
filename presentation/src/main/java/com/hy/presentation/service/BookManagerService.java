package com.hy.presentation.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.hy.presentation.IOnNewBookArrivedListener;
import com.hy.presentation.Book;
import com.hy.presentation.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean();

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    public BookManagerService() {
    }

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mBookList.add(new Book(1, "android"));
        mBookList.add(new Book(2, "ios"));

        new Thread(new ServiceWorker()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.hy.presentation.permission.ACCESS_BOOK_SERVICE");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private void onNewBookArrived(Book book) {
        mBookList.add(book);

        mListenerList.beginBroadcast();
        int count = mListenerList.getRegisteredCallbackCount();
        for (int i = 0; i < count; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            try {
                listener.onNewBookArrived(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);

                onNewBookArrived(newBook);
            }
        }
    }
}
