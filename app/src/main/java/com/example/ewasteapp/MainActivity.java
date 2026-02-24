package com.example.ewasteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//add
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerationConfig;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.Schema;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    TextView itemName;
    TextView weight;
    TextView recylcComponents;
    TextView hazMat;
    TextView reuseRec;
    TextView impact;

   // TextView ImageResultText;
    Button uploadButton;
    ImageView IVPreviewImage;
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ResultText = findViewById(R.id.ResultText);
        itemName = findViewById(R.id.itemName);
        weight = findViewById(R.id.weight);
        recylcComponents = findViewById(R.id.recylcComponents);
        hazMat = findViewById(R.id.hazMat);
        reuseRec = findViewById(R.id.reuseRec);
        impact = findViewById(R.id.impact);

        uploadButton = findViewById(R.id.uploadButton);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);


        //allows users to upload image
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
    }

    //Converts uploaded image to bitmap so it can be read by Gemini
    private Bitmap uriToBitmap(Uri uri){
        try{
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //allows user to select image from gallery
    void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE);
    }

    //will run after user uploads image
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //compare the resultCode with the SELECT_PICTURE constant
            if(requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                if(null != selectedImageUri){
                    IVPreviewImage.setImageURI(selectedImageUri);
                    Bitmap bitmap = uriToBitmap(selectedImageUri);

                    //provide a JSOM schema object using a standardised format
                    //later, pass this schema object into 'responseSchema' in the generation config
                    Schema jsonSchema = Schema.obj(
                            /*properties*/
                            Map.of(
                                    "ewaste", Schema.array(
                                            /*items*/ Schema.obj(
                                                    /*properties*/
                                                    Map.of(
                                                            "itemName", Schema.str(),
                                                            "weight", Schema.numDouble(),
                                                            "recyclableComponents", Schema.str(),
                                                            "hazardousMaterials", Schema.str(),
                                                            "reuseRecommendations", Schema.str(),
                                                            "impact",
                                                            Schema.enumeration(
                                                                    List.of("Low","Moderate","High")
                                                            )
                                                    ),
                                                    List.of(
                                                            "itemName",
                                                            "weight",
                                                            "recyclableComponents",
                                                            "hazardousMaterials",
                                                            "reuseRecommendations",
                                                            "impact"
                                                    )
                                            )
                                    )
                            ),
                            List.of("ewaste")
                    );

                    GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
                    configBuilder.responseMimeType = "application/json";
                    configBuilder.responseSchema = jsonSchema;

                    GenerationConfig generationConfig = configBuilder.build();




                    //initialise the Gemini Developer API backend service
                    GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                            .generativeModel("gemini-2.5-flash",generationConfig);

                    //GenerativeModelFutures support ListenableFuture and Publisher API
                    //provides interface for asynchronous content generation
                    GenerativeModelFutures model = GenerativeModelFutures.from(ai);
                    Executor executor = ContextCompat.getMainExecutor(this);

                    Content content = new Content.Builder()
                            .addImage(bitmap)
                            .addText("Identify this e-waste item and provide a breakdown for recycling. Include: item name, estimated weight in g, recyclable components (e.g. copper, plastic), hazardous materials (e.g. lead, mercury), 3-4 creative reuse or proper disposal recommendations, and a brief environmental impact statement.")
                            .build();

                    //generate text output
                    ListenableFuture<GenerateContentResponse> imageResponse = model.generateContent(content);

                    itemName.setText("Gemini is loading...");

                    Futures.addCallback(imageResponse, new FutureCallback<GenerateContentResponse>() {

                        @Override
                        public void onSuccess(GenerateContentResponse result){
//                            String resultText2 = result.getText();
//                            ImageResultText.setText(resultText2);

                            try{
                                //HERE. FIND OUT HOW TO OUTPUT JSON INTO A NICE FORMAT
                                String jsonText = result.getText();

                                //convert String to JSON Object
                                JSONObject root = new JSONObject(jsonText);

                                //converts JSON into JSONArray object
                                JSONArray items = root.getJSONArray("ewaste");
                                JSONObject item = items.getJSONObject(0);

                                itemName.setText(item.getString("itemName"));
                                weight.setText(item.getDouble("weight") + " g");
                                recylcComponents.setText(item.getString("recyclableComponents"));
                                hazMat.setText(item.getString("hazardousMaterials"));
                                reuseRec.setText(item.getString("reuseRecommendations"));
                                impact.setText(item.getString("impact"));

                            }catch(Exception e){
                                e.printStackTrace();
                                //add
                                itemName.setText("JSON Parse Error: " + e.getMessage());
                            }
                        }
                        @Override
                        public void onFailure(Throwable t){

                            itemName.setText("Error: " + t.getMessage());
                            Toast.makeText(MainActivity.this, "AI Request Failed", Toast.LENGTH_LONG).show();
                        }
                    }, executor);
                }
            }
        }
    }
}