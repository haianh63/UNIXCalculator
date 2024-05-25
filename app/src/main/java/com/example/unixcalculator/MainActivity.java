package com.example.unixcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText blockSize, pointerSize, logicalAddress;
    Button calculate;
    TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blockSize = findViewById(R.id.block_size);
        pointerSize = findViewById(R.id.pointer_size);
        logicalAddress = findViewById(R.id.logical_address);
        calculate = findViewById(R.id.calculate);
        result = findViewById(R.id.result);
        calculate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (blockSize.getText().toString().equals("") || pointerSize.getText().toString().equals("") || logicalAddress.getText().toString().equals(""))
                {
                    result.setText("Please fill all the fields");
                } else {
                    long blockSizeValue = Long.parseLong(blockSize.getText().toString());
                    long pointerSizeValue = Long.parseLong(pointerSize.getText().toString());
                    long logicalAddressValue = Long.parseLong(logicalAddress.getText().toString());
                    String res = UNIXCalculate(blockSizeValue, pointerSizeValue, logicalAddressValue);
                    result.setText(res);
                }
            }
        });

    }

    public String UNIXCalculate(long blockSize, long pointerSize, long logicalAddress) {
        long totalPointerPerBlock = (long) blockSize / pointerSize;
        long directPointerThreshold = 12 * blockSize - 1;
        long indirectPointerThreshold = (12 + totalPointerPerBlock) * blockSize - 1;
        long secondIndirectPointerThreshold = (12 + totalPointerPerBlock + (long) Math.pow(totalPointerPerBlock, 2)) * blockSize - 1;
        long tripleIndirectPointerThreshold = (12 + totalPointerPerBlock + (long) Math.pow(totalPointerPerBlock, 2) + (long) Math.pow(totalPointerPerBlock, 3)) * blockSize - 1;

        String res = "";
        if (0 <= logicalAddress && logicalAddress <= directPointerThreshold) {
            res += "Direct Pointer\n";
            long blockId = (long) logicalAddress / blockSize;
            long offset = logicalAddress % blockSize;
            res+= "Block id: " + blockId;
            res += "\n";
            res += "Offset: " + offset;
            res += "\n";
        } else if (directPointerThreshold < logicalAddress && logicalAddress <= indirectPointerThreshold) {
            res += "Indirect Pointer\n";
            long blockId = (long) logicalAddress / blockSize - 12;
            long offset = logicalAddress % blockSize;
            res += "Block id: " + blockId;
            res += "\n";
            res += "Offset: " + offset;
            res += "\n";

        } else if (indirectPointerThreshold < logicalAddress && logicalAddress <= secondIndirectPointerThreshold) {
            res += "Second Indirect Pointer\n";
            long blockId = ((long) logicalAddress / blockSize) - (12 + totalPointerPerBlock);
            long indexBlock = (long) blockId / totalPointerPerBlock;
            long block = blockId % totalPointerPerBlock;
            long offset = logicalAddress % blockSize;
            res += "Index block: " + indexBlock;
            res += "\n";
            res += "Block: " + block;
            res += "\n";
            res += "Offset: " + offset;
            res += "\n";

        } else if (secondIndirectPointerThreshold < logicalAddress && logicalAddress <= tripleIndirectPointerThreshold) {
            res += "Triple Indirect Pointer\n";
            long blockId = ((long) logicalAddress / blockSize) - (12 + totalPointerPerBlock + (long) Math.pow(totalPointerPerBlock, 2));
            long index2 = (int) blockId / ((long) Math.pow(totalPointerPerBlock, 2));
            long index3 = (int) (blockId % ((long) Math.pow(totalPointerPerBlock, 2))) / totalPointerPerBlock;
            long block = blockId % totalPointerPerBlock;
            long offset = logicalAddress % blockSize;
            res += "Index Block Level 2: " + index2;
            res += "\n";
            res += "Index Block Level 3: " + index3;
            res += "\n";
            res += "Block: " + block;
            res += "\n";
            res += "Offset: " + offset;
            res += "\n";

        } else {
            res += "Invalid logical address\n";
        }
        return res;
    }
}