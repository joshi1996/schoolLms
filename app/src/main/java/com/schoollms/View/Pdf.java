package com.schoollms.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.schoollms.R;
import com.nayaastra.skewpdfview.SkewPdfView;

public class Pdf extends AppCompatActivity {

    SkewPdfView skewPdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        String pdfLink = getIntent().getStringExtra("pdfLink");

        skewPdfView = findViewById(R.id.skewPdfView);

        skewPdfView.loadPdf(pdfLink);

    }
}