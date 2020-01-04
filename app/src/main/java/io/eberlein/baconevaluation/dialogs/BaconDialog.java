package io.eberlein.baconevaluation.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.eberlein.baconevaluation.R;
import io.eberlein.baconevaluation.objects.Bacon;
import io.eberlein.baconevaluation.objects.events.EventOpenCamera;


public class BaconDialog {
    private Bacon bacon;
    private Context ctx;
    private AlertDialog dialog;

    @BindView(R.id.et_name) EditText name;
    @BindView(R.id.et_code_format) EditText codeFormat;
    @BindView(R.id.et_code) EditText code;
    @BindView(R.id.et_description) EditText description;
    @BindView(R.id.sb_rating) SeekBar sbRating;
    @BindView(R.id.iv_product) ImageView product;
    @BindView(R.id.tv_rating) TextView rating;

    private void saveBacon(){
        bacon.getRealm().beginTransaction();
        bacon.setName(name.getText().toString());
        bacon.setDescription(description.getText().toString());
        bacon.setRating(sbRating.getProgress());
        bacon.getRealm().commitTransaction();
    }

    @OnClick(R.id.iv_product)
    void onProductClicked(){
        saveBacon();
        dialog.dismiss();
        EventBus.getDefault().post(new EventOpenCamera());
    }

    public BaconDialog(Bacon bacon, Context ctx){
        this.bacon = bacon;
        this.ctx = ctx;
    }

    public void build(){
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_bacon, null, false);
        ButterKnife.bind(this, v);
        name.setText(bacon.getName());
        codeFormat.setText(bacon.getCodeFormat());
        code.setText(String.valueOf(bacon.getCode()));
        description.setText(bacon.getDescription());
        sbRating.setProgress(bacon.getRating());
        sbRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rating.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rating.setText(String.valueOf(bacon.getRating()));
        if(bacon.getPictureUri() != null) product.setImageURI(Uri.parse(bacon.getPictureUri()));
        dialog = new AlertDialog.Builder(ctx).setView(v).setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveBacon();
                dialog.dismiss();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
