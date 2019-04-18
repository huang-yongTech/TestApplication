package com.hy.presentation.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hy.base.Constant;
import com.hy.base.util.FixMemLeak;
import com.hy.data.entity.ItemType;
import com.hy.library.widget.LinearItemDecoration;
import com.hy.presentation.R;
import com.hy.presentation.adapter.ItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int UNKNOWN_APP_SOURCES_REQUEST_CODE = 0x100;
    public static final int INSTALL_PACKAGES_REQUEST_CODE = 0x101;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;

    private List<ItemType> mItemTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        packageInstallRequest();
        init();
    }

    /**
     * 8.0版本后应用安装权限申请
     */
    private void packageInstallRequest() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();

            if (!haveInstallPermission) {
                //请求允许安装未知来源应用权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        INSTALL_PACKAGES_REQUEST_CODE);
            } else {
                Log.i(TAG, "packageInstallRequest: 安装APP");
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageUri = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, UNKNOWN_APP_SOURCES_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case INSTALL_PACKAGES_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: 安装APP");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startInstallPermissionSettingActivity();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case UNKNOWN_APP_SOURCES_REQUEST_CODE:
                Log.i(TAG, "onActivityResult: 安装APP");
                break;
        }
    }

    private void init() {
        mToolbar.setTitle("主界面");
        setSupportActionBar(mToolbar);

        mItemTypeList = new ArrayList<>();
        mItemTypeList.add(new ItemType("Fragment回退栈测试", Constant.TYPE_FRAGMENT_TEST));
        mItemTypeList.add(new ItemType("Room测试", Constant.TYPE_ROOM_TEST));
        mItemTypeList.add(new ItemType("HandlerThread测试", Constant.TYPE_HANDLER_THREAD));
        mItemTypeList.add(new ItemType("WebView测试", Constant.TYPE_WEB_VIEW));
        mItemTypeList.add(new ItemType("RecyclerView测试", Constant.TYPE_RECYCLER_VIEW));
        mItemTypeList.add(new ItemType("RecyclerView缓存", Constant.TYPE_RECYCLER_VIEW_CACHE));
        mItemTypeList.add(new ItemType("仿即刻点赞效果", Constant.TYPE_PRAISE));
        mItemTypeList.add(new ItemType("多边形网格", Constant.TYPE_POLYGON));
        mItemTypeList.add(new ItemType("图片裁剪动画", Constant.TYPE_CROP_PIC));
        mItemTypeList.add(new ItemType("动画进阶", Constant.TYPE_ADVANCE_ANIM));

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        LinearItemDecoration itemDecoration = new LinearItemDecoration(this, LinearItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        ItemTypeAdapter adapter = new ItemTypeAdapter(R.layout.item_main_text_layout);
        mRecyclerView.setAdapter(adapter);

        adapter.setNewData(mItemTypeList);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ItemType itemType = mItemTypeList.get(position);
                switch (itemType.getType()) {
                    case Constant.TYPE_FRAGMENT_TEST:
                        startActivity(new Intent(MainActivity.this, FragmentTestActivity.class));
                        break;
                    case Constant.TYPE_ROOM_TEST:
                        startActivity(new Intent(MainActivity.this, RoomTestActivity.class));
                        break;
                    case Constant.TYPE_HANDLER_THREAD:
                        Bundle commonBundle = new Bundle();
                        commonBundle.putString(Constant.TYPE, Constant.TYPE_HANDLER_THREAD);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, commonBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_WEB_VIEW:
                        Bundle handlerBundle = new Bundle();
                        handlerBundle.putString(Constant.TYPE, Constant.TYPE_WEB_VIEW);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, handlerBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_RECYCLER_VIEW:
                        Bundle recyclerBundle = new Bundle();
                        recyclerBundle.putString(Constant.TYPE, Constant.TYPE_RECYCLER_VIEW);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, recyclerBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_RECYCLER_VIEW_CACHE:
                        Bundle recycleCacheBundle = new Bundle();
                        recycleCacheBundle.putString(Constant.TYPE, Constant.TYPE_RECYCLER_VIEW_CACHE);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, recycleCacheBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_PRAISE:
                        Bundle praiseBundle = new Bundle();
                        praiseBundle.putString(Constant.TYPE, Constant.TYPE_PRAISE);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, praiseBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_POLYGON:
                        Bundle polygonBundle = new Bundle();
                        polygonBundle.putString(Constant.TYPE, Constant.TYPE_POLYGON);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, polygonBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_CROP_PIC:
                        Bundle cropPicBundle = new Bundle();
                        cropPicBundle.putString(Constant.TYPE, Constant.TYPE_CROP_PIC);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, cropPicBundle)
                                .navigation();
                        break;
                    case Constant.TYPE_ADVANCE_ANIM:
                        Bundle advanceAnimBundle = new Bundle();
                        advanceAnimBundle.putString(Constant.TYPE, Constant.TYPE_ADVANCE_ANIM);
                        ARouter.getInstance()
                                .build("/presentation/commonHost")
                                .withBundle(Constant.TYPE_COMMON_BUNDLE, advanceAnimBundle)
                                .navigation();
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FixMemLeak.fixLeak(this);
    }
}

