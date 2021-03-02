package com.cygnus.coursefeature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.cygnus.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoursePdf extends AppCompatActivity  {
String course_desc,course_url;
PDFView pdfView;
int pagenumber=0;
Toolbar toolbar_crvideo;
TextView tv_pgno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_pdf2);
        course_desc=getIntent().getStringExtra("course_desc");
        course_url=getIntent().getStringExtra("course_url");
        pdfView=findViewById(R.id.pdfView);
        tv_pgno=findViewById(R.id.tv_pgno);
        toolbar_crvideo=findViewById(R.id.toolbar_crvideo);

        toolbar_crvideo.setTitle(course_desc);
        new RetrievePDFStream().execute(course_url);

    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> implements OnPageChangeListener,OnLoadCompleteListener {

        ProgressDialog progressDialog;
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(CoursePdf.this);
            progressDialog.setTitle("Getting the content...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .defaultPage(pagenumber)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .enableSwipe(true)
                    .onPageChange(this)
                    .onLoad(this)
                    .load();
            progressDialog.dismiss();
        }

        @Override
        public void onPageChanged(int page, int pageCount) {
            pagenumber = page;
            tv_pgno.setText( String.valueOf(page + 1));
        }

        @Override
        public void loadComplete(int nbPages) {
            pdfView.fitToWidth(nbPages);
        }
    }
}

