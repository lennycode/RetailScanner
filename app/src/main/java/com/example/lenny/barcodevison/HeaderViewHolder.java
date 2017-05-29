package com.example.lenny.barcodevison;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lenny on 5/28/17.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView itemsHeader;

    public HeaderViewHolder(View itemView) {

        super(itemView);
        itemsHeader = (TextView) itemView.findViewById(R.id.txtSeperator);

    }

}