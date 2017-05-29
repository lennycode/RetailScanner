package com.example.lenny.barcodevison;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenny.barcodevison.datainterface.ProdBucket;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class ResultAdapter extends Adapter<ViewHolder> {


//    Context activityContext;
//    List<ProdBucket> dataList;
//
//    public ResultAdapter(Context context, List<ProdBucket> prodBucketList) {
//        activityContext = context;
//        dataList = prodBucketList;
//
//    }
//
//    @Override
//    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(activityContext);
//
//        // Inflate the custom layout
//        View contactView = inflater.inflate(R.layout.selection_card, parent, false);
//
//        // Return a new holder instance
//        ViewHolder viewHolder = new ViewHolder(contactView);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        // Get the data model based on position
//        ProdBucket item = dataList.get(position);
//
//        // Set item views based on your views and data model
//        TextView description = holder.itemDescription;
//        TextView price = holder.itemPrice;
//        description.setText(item.itemDescription);
//        price.setText(item.itemPrice);
//        if (item.itemPic != null) {
//
//            Picasso.with(activityContext)
//                    .load(item.itemPic.replace("\"","").replace("180","92"))
//
//                    .into(holder.productImage);
//        } else {
//
//
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView itemDescription;
//
//        public TextView itemPrice;
//
//        public ImageView productImage;
//
//        public ImageView tagImage;
//
//
//        public ViewHolder(View itemView) {
//
//            super(itemView);
//            itemDescription = (TextView) itemView.findViewById(R.id.txtDescription);
//            itemPrice = (TextView) itemView.findViewById(R.id.txtPrice);
//            productImage = (ImageView) itemView.findViewById(R.id.imgProduct);
//
//        }
//    }


    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_DIVIDER = 1;
    Context activityContext;
    List<ProdBucket> dataList  = null;
    int position;

    public ResultAdapter(Context context, List<ProdBucket> prodBucketList) {
        activityContext = context;

            dataList = prodBucketList;

    }

    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View contactView;
        if (viewType == ITEM_TYPE_NORMAL) {
            // Inflate the custom layout
            contactView = inflater.inflate(R.layout.selection_card, null);
            return new ItemViewHolder(contactView);

        } else {
            contactView = inflater.inflate(R.layout.seperator_card, null);
            return new HeaderViewHolder(contactView);
        }



    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (dataList.get(position).isDivider) {
            return ITEM_TYPE_DIVIDER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the data model based on position
        ProdBucket item = dataList.get(position);
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_DIVIDER ) {
            TextView description = ((HeaderViewHolder)holder).itemsHeader;
            description.setText(item.itemDescription);
        } else {
            // Set item views based on your views and data model
            TextView description = ((ItemViewHolder)holder).itemDescription;
            TextView price = ((ItemViewHolder)holder).itemPrice;
            description.setText(item.itemDescription);
            price.setText(item.itemPrice);
            if (item.itemPic != null) {

                Picasso.with(activityContext)
                        .load(item.itemPic.replace("\"", "").replace("180", "92"))

                        .into(((ItemViewHolder)holder).productImage);
            } else {


            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
