package KumaTraining13.Bear;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class Beartivity2 extends AppCompatActivity {
    private static final String TAG = "PdfCreatorActivity";
    private EditText edtText;
    private Button btn;
    private File file;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beartivity2);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        btn = findViewById(R.id.btn_export);
        edtText = findViewById(R.id.edt_text);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtText.getText().toString().isEmpty()){
                    edtText.setError("Tolong Isi Text Dulu");
                    edtText.requestFocus();
                }else{
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void createPdfWrapper() throws FileNotFoundException,DocumentException{
        //Mengambil Info Izin Storage
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //Check Apakah Diizinkan
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            //Jika Tidak Diizinkan
            //Check Apakah SDK HP Support Fungsi PDF Ini
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Meminta Izin Untuk Mengakses Storage Jika Belum
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Check Ulang SDK HP(Agak Redundant)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });

                    //Else Untuk Memaksa Izin Jika Dialog Pesan Request Tidak Keluar
                }else{
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
            //Jika Diizinkan
        }else {
            createPdf();
        }
    }
    //Fungsi yang Udah Ada Dari Android Yang di Override
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //Switch Untuk Perizinan
        switch (requestCode) {
            //Case Jika Request Codenya Adalah 111(Request Perizinan Storage)
            case REQUEST_CODE_ASK_PERMISSIONS:
                //JikaDiizinkan
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }

                    //JikaTidak
                } else {
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
                //Case Jika Request Code Bukan Untuk Storage
            default:
                //Note : Mungkin Agak Looping?
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Keluar", null)
                .create()
                .show();
    }
    private void createPdf() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Buat Folder PDF Baru");
        }
        file = new File(docsFolder.getAbsolutePath(),"PDFgenerate.pdf");
        OutputStream output = new FileOutputStream(file);
        Document document = new Document();
        //Kayanya Gak Guna Soalnya Ini Return, Bukan Void
        //Nevermind
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph(edtText.getText().toString()));
        document.close();
        previewPdf();
    }
    private void previewPdf() {
        PackageManager packageManager = getPackageManager();
        Intent testpdfIntent = new Intent(Intent.ACTION_VIEW);
        testpdfIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testpdfIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);
        }else{
            Toast.makeText(this,"Download aplikasi pdf viewer untuk melihat hasil generate",Toast.LENGTH_SHORT).show();
        }
    }
}

