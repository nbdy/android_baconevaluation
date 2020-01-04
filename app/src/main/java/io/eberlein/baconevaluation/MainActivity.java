package io.eberlein.baconevaluation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.baconevaluation.adapters.BaconAdapter;
import io.eberlein.baconevaluation.dialogs.BaconDialog;
import io.eberlein.baconevaluation.objects.Bacon;
import io.eberlein.baconevaluation.objects.events.EventBaconSelected;
import io.eberlein.baconevaluation.objects.events.EventOpenCamera;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Realm realm;
    private BaconAdapter adapter;

    private Bacon currentBacon;

    private static final int REQUEST_CODE_BARCODE = 420;
    private static final int REQUEST_CODE_CAMERA = 666;

    @BindView(R.id.recycler) RecyclerView recycler;

    @OnClick(R.id.btn_add)
    void btnAddBaconClicked(){
        new IntentIntegrator(this)
                .setBeepEnabled(false)
                .setRequestCode(REQUEST_CODE_BARCODE)
                .setCameraId(0)
                .setPrompt("scan your meat")
                .setBarcodeImageEnabled(true)
                .initiateScan();
    }

    private void addBacon(Long code, String codeFormat){
        Bacon bacon = realm.where(Bacon.class).equalTo("code", code).findFirst();
        if(bacon == null) {
            bacon = new Bacon(code, codeFormat);
            realm.beginTransaction();
            bacon = realm.copyToRealm(bacon);
            realm.commitTransaction();
        }
        currentBacon = bacon;
        new BaconDialog(bacon, this).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "requestCode: " + requestCode);
        if(requestCode == REQUEST_CODE_BARCODE){
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            if(result.getContents() == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, result.getContents());
                Log.d(TAG, result.getFormatName());
                Toast.makeText(this, "scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                addBacon(Long.valueOf(result.getContents()), result.getFormatName());
            }
        } else if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK){
            new BaconDialog(currentBacon, this).build();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        adapter = new BaconAdapter(realm.where(Bacon.class).findAll());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventOpenCamera(EventOpenCamera e){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photo = null;
                try{
                    photo = Static.createImageFile(this);
                } catch (IOException ex){
                    ex.printStackTrace();
                    Toast.makeText(this, "could not create photo file", Toast.LENGTH_LONG).show();
                }
                if(photo != null){
                    Uri photoUri = FileProvider.getUriForFile(this, "io.eberlein.baconevaluation.fileprovider", photo);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    realm.beginTransaction();
                    currentBacon.setPictureUri(photoUri.toString());
                    realm.commitTransaction();
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                }
            }
        } else {
            Toast.makeText(this, "this phone has no camera", Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBaconSelected(EventBaconSelected e){
        currentBacon = e.getBacon();
        new BaconDialog(currentBacon, this).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
