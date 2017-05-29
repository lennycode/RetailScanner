package com.example.lenny.barcodevison;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lenny on 5/28/17.
 */

public class ItemViewHolder   extends RecyclerView.ViewHolder {

        public TextView itemDescription;

        public TextView itemPrice;

        public ImageView productImage;

        public ImageView tagImage;


        public ItemViewHolder(View itemView) {

            super(itemView);
            itemDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            itemPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            productImage = (ImageView) itemView.findViewById(R.id.imgProduct);

        }

}
