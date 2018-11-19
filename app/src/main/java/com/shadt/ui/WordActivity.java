//package com.shadt.ui;
//
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.shadt.caibian_news.R;
//import com.shadt.ui.utils.FileUtils;
//import com.shadt.util.MyLog;
//
//import org.apache.poi.hwpf.HWPFDocument;
//import org.apache.poi.hwpf.usermodel.Range;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Vector;
//
//public class WordActivity extends AppCompatActivity {
//    // 模板文集地址
//    private static final String demoPath = "/mnt/sdcard/doc/test.doc";
//    // 创建生成的文件地址
//    private String newPath = "/mnt/sdcard/doc/testS.doc";
//    private Button btn, btns;
//
//    EditText ed_title, ed_content, ed_location;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_word);
//        btn = (Button) findViewById(R.id.btn);
//        btns = (Button) findViewById(R.id.btns);
//        ed_title = findViewById(R.id.ed_title);
//        ed_content = findViewById(R.id.ed_content);
//        ed_location = findViewById(R.id.ed_location);
//        btn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//              /*  try {
//                    InputStream inputStream = getAssets().open("test.doc");
//                    FileUtils.writeFile(new File(demoPath), inputStream);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                doScan();*/
////              writeTxt();
//                writeTxtToFile(ed_content.getText().toString(),path,"b.txt");
//            }
//        });
//        btns.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//
//                ed_title.setText(ReadTxtFile(path+"b.txt"));
//                getFileName(path);
//            }
//        });
//
//    }
//
//    private void doScan() {
//        //获取模板文件
//        File demoFile = new File(demoPath);
//        //创建生成的文件
//        newPath = "/mnt/sdcard/doc/" + System.currentTimeMillis() + ".doc";
//        File newFile = new File(newPath);
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("$BT$", ed_title.getText().toString());
//        map.put("$CT$", ed_content.getText().toString());
//        map.put("$LC$", ed_location.getText().toString());
//        map.put("$NM$", "爸爸打打大神");
//        map.put("$TM$", "2000-11-10");
//        writeDoc(demoFile, newFile, map);
//        //查看
//        doOpenWord();
//    }
//    public static Vector<String> getFileName(String fileAbsolutePath) {
//        Vector<String> vecFile = new Vector<String>();
//        File file = new File(fileAbsolutePath);
//        File[] subFile = file.listFiles();
//
//        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
//            // 判断是否为文件夹
//            if (!subFile[iFileLength].isDirectory()) {
//                String filename = subFile[iFileLength].getName();
//                MyLog.e("eee"+"文件名 ： " + filename);
//            }
//        }
//        return vecFile;
//    }
//
//    /**
//     * 调用手机中安装的可打开word的软件
//     */
//    private void doOpenWord() {
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        String fileMimeType = "application/msword";
//        intent.setDataAndType(Uri.fromFile(new File(newPath)), fileMimeType);
//        try {
//            WordActivity.this.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            //检测到系统尚未安装OliveOffice的apk程序
//            Toast.makeText(WordActivity.this, "未找到软件", Toast.LENGTH_LONG).show();
//            //请先到www.olivephone.com/e.apk下载并安装
//        }
//    }
//    String path="/mnt/sdcard/mytxt/";
//    public void writeTxt() {
//        try {
//             File file = new File(path+"a.txt");
//            if ("/mnt/sdcard/mytxt/a.txt".indexOf(".") != -1) {
//                // 说明包含，即使创建文件, 返回值为-1就说明不包含.,即使文件
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                MyLog.i("创建了文件");
//
//            } else {
//                // 创建文件夹
//                file.mkdir();
//                MyLog.i("创建了文件夹");
//                System.out.println("创建了文件夹");
//            }
//            File writename = new File(path+"a.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
//            writename.createNewFile(); // 创建新文件
//            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
//            out.write(""+ed_content.getText().toString()); // \r\n即为换行
//            out.flush(); // 把缓存区内容压入文件
//            out.close(); // 最后记得关闭文件
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    // 将字符串写入到文本文件中
//    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
//        //生成文件夹之后，再生成文件，不然会出错
//        makeFilePath(filePath, fileName);
//
//        String strFilePath = filePath+fileName;
//        // 每次写入时，都换行写
//        String strContent = strcontent + "\r\n";
//        try {
//            File file = new File(strFilePath);
//            if (!file.exists()) {
//
//                MyLog.i("Create the file:" + strFilePath);
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            raf.seek(file.length());
//            raf.write(strContent.getBytes());
//            raf.close();
//        } catch (Exception e) {
//            MyLog.i("Error on write File:" + e);
//        }
//    }
//    // 生成文件
//    public File makeFilePath(String filePath, String fileName) {
//        File file = null;
//        makeRootDirectory(filePath);
//        try {
//            file = new File(filePath + fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return file;
//    }
//
//    // 生成文件夹
//    public static void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//        } catch (Exception e) {
//            MyLog.i(e+"");
//        }
//    }
//    String str;
//    public  String ReadTxtFile(String strFilePath)
//    {
//        String path = strFilePath;
//        List<String> newList=new ArrayList<String>();
//        //打开文件
//        File file = new File(path);
//        //如果path是传递过来的参数，可以做一个非目录的判断
//        if (file.isDirectory())
//        {
//            Log.d("TestFile", "The File doesn't not exist.");
//        }
//        else
//        {
//            try {
//                InputStream instream = new FileInputStream(file);
//                if (instream != null)
//                {
//                    InputStreamReader inputreader = new InputStreamReader(instream);
//                    BufferedReader buffreader = new BufferedReader(inputreader);
//                    String line;
//                    //分行读取
//                    while (( line = buffreader.readLine()) != null) {
//                        newList.add(line+"\n");
//                        str=str+line+"\n";
//                    }
//                    instream.close();
//                }
//            }
//            catch (java.io.FileNotFoundException e)
//            {
//                Log.d("TestFile", "The File doesn't not exist.");
//            }
//            catch (IOException e)
//            {
//                Log.d("TestFile", e.getMessage());
//            }
//        }
//        return str;
//    }
//
//    public void getTxt() {
//        try {
//            String pathname = path+"a.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
//            File filename = new File(pathname); // 要读取以上路径的input。txt文件
//            InputStreamReader reader = new InputStreamReader(
//                    new FileInputStream(filename)); // 建立一个输入流对象reader
//            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
//            String line = "";
//            line = br.readLine();
//            while (line != null) {
//                line = br.readLine(); // 一次读入一行数据
//                MyLog.i("》"+line);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * demoFile 模板文件
//     * newFile 生成文件
//     * map 要填充的数据
//     */
//    public void writeDoc(File demoFile, File newFile, Map<String, String> map) {
//        try {
//            FileInputStream in = new FileInputStream(demoFile);
//            HWPFDocument hdt = new HWPFDocument(in);
//            // Fields fields = hdt.getFields();
//            // 读取word文本内容
//            Range range = hdt.getRange();
//            // System.out.println(range.text());
//
//            // 替换文本内容
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                range.replaceText(entry.getKey(), entry.getValue());
//            }
//            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//            FileOutputStream out = new FileOutputStream(newFile, true);
//            hdt.write(ostream);
//            // 输出字节流
//            out.write(ostream.toByteArray());
//            out.close();
//            ostream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
