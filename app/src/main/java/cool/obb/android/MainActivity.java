package cool.obb.android;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView mShowImageView;
    private Button mChangeBTN;
    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowImageView = findViewById(R.id.iv_show);
        mChangeBTN = findViewById(R.id.btn_change);
        checkPermission();
    }

    public void checkPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        initData();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();
    }

    public void initData() {
        String obb_filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/" + getPackageName() + "/main.22070479.cool.obb.android.obb";
        String dstFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhOOB";
        unZipObb(obb_filename, dstFolderPath);

        String srcPNG1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhOOB/MYOBB/11.png";
        String srcPNG2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhOOB/MYOBB/12.png";
        String srcPNG3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhOOB/MYOBB/13.png";
        String srcPNG4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhOOB/MYOBB/14.png";

        mChangeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = mCount % 4;
                switch (position) {
                    case 0:
                        Glide.with(MainActivity.this).load(srcPNG1).into(mShowImageView);
                        break;
                    case 1:
                        Glide.with(MainActivity.this).load(srcPNG2).into(mShowImageView);
                        break;
                    case 2:
                        Glide.with(MainActivity.this).load(srcPNG3).into(mShowImageView);
                        break;
                    case 3:
                        Glide.with(MainActivity.this).load(srcPNG4).into(mShowImageView);
                        break;
                }
                mCount++;

            }
        });
    }

    public void unZipObb(String srcFilePath, String dstFolderPath) {
        Log.d("MainActivity", "unZipObb : srcFilePath=" + srcFilePath + ", dstFolderPath=" + dstFolderPath);
        try {
            String obbFilePath = srcFilePath;
            if (obbFilePath == null) {
                Log.d("MainActivity", "unZipObb error : obbFilePath == null");
                return;
            } else {
                File obbFile = new File(obbFilePath);
                if (!obbFile.exists()) {
                    //下载obb文件
                    Log.d("MainActivity", "unZipObb error : !obbFile.exists()");
                } else {
                    File outputFolder = new File(dstFolderPath);
                    if (!outputFolder.exists()) {
                        //目录未创建 没有解压过
                        outputFolder.mkdirs();
                        unZip(obbFile, outputFolder.getAbsolutePath());
                    } else {
                        //目录已创建 判断是否解压过
                        if (outputFolder.listFiles() == null) {
                            //解压过的文件被删除
                            unZip(obbFile, outputFolder.getAbsolutePath());
                        } else {
                            //此处可添加文件对比逻辑
                            Log.d("MainActivity", "unZipObb error : outputFolder.listFiles() != null");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("MainActivity", "unZipObb error : Exception");
            e.printStackTrace();
        }
    }

    public void createDirectoryIfNeeded(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
    }

    public void unZip(File zipFile, String outPathString) {
        Log.d("MainActivity", "unZip " + zipFile.getName() + " to " + outPathString);
        try {
            createDirectoryIfNeeded(outPathString);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry;
            String szName;
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    File file = new File(outPathString + File.separator + szName);
                    createDirectoryIfNeeded(file.getParent());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = inZip.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}