package com.dodola.patcher;

import java.io.File;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    static {
        //引入打包库
        System.loadLibrary("Patcher");
    }
    private String rootPath;

    private Button btn;
    private EditText mTxtOld;
    private EditText mTxtPatcher;
    private EditText mTxtNew;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
            case 0:
                mTxtOld.setText(data.getData().getEncodedPath());
                break;

            case 1:
                mTxtPatcher.setText(data.getData().getEncodedPath());
                break;

            }
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        setContentView(R.layout.activity_main);
        btn = (Button) this.findViewById(R.id.button1);
        mTxtOld = (EditText) this.findViewById(R.id.text_old);
        mTxtOld.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 0);

            }
        });
        mTxtNew = (EditText) this.findViewById(R.id.text_new);

        mTxtPatcher = (EditText) this.findViewById(R.id.text_patcher);
        mTxtPatcher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(mTxtOld.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请选择旧版本文件", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mTxtNew.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请输入合并后新版本文件名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mTxtPatcher.getText().toString())) {
                    Toast.makeText(MainActivity.this, "请选择补丁文件", Toast.LENGTH_SHORT).show();
                    return;
                }

                AsyncTask task = new AsyncTask() {

                    private ProgressDialog progressDialog;

                    @Override
                    protected void onPostExecute(Object result) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "打包完成，安装。。。。", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String filePath = rootPath + File.separator + mTxtNew.getText().toString() + ".apk";
                        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
                        startActivity(i);

                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = ProgressDialog.show(MainActivity.this, "正在生成APK...", "请稍等...", true, false);
                        progressDialog.show();
                    }

                    @Override
                    protected Object doInBackground(Object... arg0) {
                        String newApk = rootPath + File.separator + mTxtNew.getText().toString() + ".apk";
                        File file = new File(newApk);
                        if (file.exists())
                            file.delete();

                        patcher(mTxtOld.getText().toString(), newApk, mTxtPatcher.getText().toString());
                        return null;
                    }
                };
                task.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public native void patcher(String old, String newapk, String patch);

}
