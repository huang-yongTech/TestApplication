package com.hy.presentation.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hy.presentation.Book;
import com.hy.presentation.IBookManager;
import com.hy.presentation.IOnNewBookArrivedListener;
import com.hy.presentation.R;
import com.hy.presentation.service.BookManagerService;

import java.util.List;

public class AidlActivity extends AppCompatActivity {
    private static final String TAG = "AidlActivity";
    public static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);

        Intent intent = new Intent(AidlActivity.this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i(TAG, "received new book : " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            mRemoteBookManager = bookManager;
            try {
                List<Book> bookList = bookManager.getBookList();
                Log.i(TAG, "query book list, list type : " + bookList.getClass().getCanonicalName());
                Log.i(TAG, "query book list, list type : " + bookList.toString());
                Book newBook = new Book(3, "Android 进阶");
                bookManager.addBook(newBook);
                Log.i(TAG, "add new book : " + newBook);
                List<Book> newList = bookManager.getBookList();
                Log.i(TAG, "query book list, list type : " + newList.toString());
                bookManager.registerListener(mListener);

                bookManager.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBookManager = null;
        }
    };

    //aidl通信死亡代理
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mRemoteBookManager == null) {
                return;
            }

            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
        }
    };

    private IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unRegisterListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);

        mHandler.removeCallbacksAndMessages(null);
    }
}
