package com.rrmsense.friendfinder.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rrmsense.friendfinder.R;
import com.rrmsense.friendfinder.models.UserInformation;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Talha on 2/26/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList<UserInformation> userInformationArray;
    private Context context;
    private GoogleMap map;

    public NotificationAdapter(ArrayList<UserInformation> userInformationArray, Context context, GoogleMap map) {
        this.userInformationArray = userInformationArray;
        this.context = context;
        this.map = map;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_information, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInformation userInformation = userInformationArray.get(position);

        holder.call.setText("Call");
        holder.notify.setText("Notify");
        holder.email.setText(userInformation.getEmail());
        holder.name.setText(userInformation.getName());
        final ImageView image = holder.image;
        Glide.with(context).load(userInformation.getImage()).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new BitmapImageViewTarget(image) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                image.setImageDrawable(circularBitmapDrawable);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userInformationArray.size();
    }

    private void sendNotification(String title, String message, String token) {
        String key = "AAAAIJtQkOg:APA91bEOre-XL80Jo7ATj2eJX0NjubhaVgtDOFZGhyYWYCfMA6CWDgDEYbbmRS9INd-xXvRxbt-Z4GwnSrv2AJdKAo4t781iRd02Fka_2RcOokC0f-rAlGk0rc1gn-SQRykeWIlx_qsQ";
        RequestParams params = new RequestParams();
        params.put("title", title);
        params.put("body", message);
        params.put("to", token);
        params.put("key", key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://appserver.rrmelectronics.com/firebase/notification.php", params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        Toast.makeText(context, "Notication", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    }
                }
        );

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name;
        private TextView email;
        private Button call;
        private Button notify;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.image);
            email = (TextView) itemView.findViewById(R.id.email);
            name = (TextView) itemView.findViewById(R.id.name);
            call = (Button) itemView.findViewById(R.id.call);
            notify = (Button) itemView.findViewById(R.id.notify);

            call.setOnClickListener(this);
            notify.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.call:
                    //Toast.makeText(context, "Button", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userInformationArray.get(getAdapterPosition()).getMobile()));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    context.startActivity(intent);
                case R.id.notify:
                    //Toast.makeText(context, "Notify", Toast.LENGTH_SHORT).show();
                    sendNotification("title","body",userInformationArray.get(getAdapterPosition()).getToken());
                    break;
                default:
                    //Toast.makeText(context, "View", Toast.LENGTH_SHORT).show();

                    LatLng latLng = new LatLng(userInformationArray.get(getAdapterPosition()).getLocationGPS().getLatitude(), userInformationArray.get(getAdapterPosition()).getLocationGPS().getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    map.animateCamera(cameraUpdate);
                    break;

            }

        }
    }
}