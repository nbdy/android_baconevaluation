package io.eberlein.baconevaluation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.eberlein.baconevaluation.R;
import io.eberlein.baconevaluation.objects.Bacon;
import io.eberlein.baconevaluation.objects.events.EventBaconSelected;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;


public class BaconAdapter extends RealmRecyclerViewAdapter<Bacon, BaconAdapter.ViewHolder> {
    public BaconAdapter(OrderedRealmCollection<Bacon> data){
        super(data, true);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Bacon bacon;
        private boolean extraMenuVisible;

        @BindView(R.id.tv_name) TextView name;
        @BindView(R.id.tv_rating) TextView rating;
        @BindView(R.id.btn_delete) Button delete;

        @OnClick
        void onClick(){
            if(!extraMenuVisible) EventBus.getDefault().post(new EventBaconSelected(bacon));
            else closeExtraMenu();
        }

        @OnLongClick
        void onLongClick(){
            if(!extraMenuVisible) openExtraMenu();
            else closeExtraMenu();
        }

        @OnClick(R.id.btn_delete)
        void onBtnDeleteClicked(){
            Realm r = bacon.getRealm();
            r.beginTransaction();
            bacon.deleteFromRealm();
            r.commitTransaction();
        }

        private void openExtraMenu(){
            extraMenuVisible = true;
            rating.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        }

        private void closeExtraMenu(){
            extraMenuVisible = false;
            rating.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
        }

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }

        void setBacon(Bacon bacon){
            this.bacon = bacon;
            name.setText(bacon.getName());
            rating.setText(String.valueOf(bacon.getRating()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setBacon(getItem(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bacon, parent, false));
    }
}
