package com.zjf.transaction.shopcart;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjf.transaction.R;
import com.zjf.transaction.base.BaseAdapter;
import com.zjf.transaction.base.BaseViewHolder;
import com.zjf.transaction.main.model.Commodity;
import com.zjf.transaction.shopcart.listener.ShopcartItemClickListener;
import com.zjf.transaction.shopcart.model.ShopcartItem;
import com.zjf.transaction.user.model.UserInfo;
import com.zjf.transaction.util.ImageLoaderUtil;
import com.zjf.transaction.util.TextUtil;

/**
 * Created by zhengjiafeng on 2019/3/28
 *
 * @author 郑佳锋 zhengjiafeng@bytedance.com
 */
public class ShopcartAdapter extends BaseAdapter<ShopcartItem> {

    private ShopcartItemClickListener listener;

    @NonNull
    @Override
    public BaseViewHolder<ShopcartItem> onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shopcart_item, parent, false);
        return new ShopcartHolder(view);
    }

    class ShopcartHolder extends BaseViewHolder<ShopcartItem> {

        private ViewGroup commodityLayout;
        private ViewGroup userLayout;
        private ViewGroup commodityInfoLayout;
        private ImageView ivUserPic, ivCommodityPic;
        private TextView tvUserName, tvCommodityMsg, tvCommodityMoney;
        private CheckBox cbChooseCommodity;

        public ShopcartHolder(View itemView) {
            super(itemView);
            commodityLayout = itemView.findViewById(R.id.layout_commodity_item);
            //user layout
            userLayout = commodityLayout.findViewById(R.id.layout_user);
            ivUserPic = userLayout.findViewById(R.id.iv_user_pic);
            tvUserName = userLayout.findViewById(R.id.tv_user_name);
            userLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2019/3/28 跳转到用户信息界面
                }
            });


            //commodity layout
            commodityInfoLayout = commodityLayout.findViewById(R.id.layout_commodity_info);
            ivCommodityPic = commodityInfoLayout.findViewById(R.id.iv_commodity_pic);
            tvCommodityMsg = commodityInfoLayout.findViewById(R.id.tv_commodity_msg);
            tvCommodityMoney = commodityInfoLayout.findViewById(R.id.tv_commodity_money);

            commodityInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2019/3/28 跳转到商品详情界面
                }
            });
            commodityInfoLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO: 2019/3/28 弹窗显示是否删除等信息
                    return false;
                }
            });
        }


        @Override
        public void onBind(ShopcartItem data) {
            UserInfo userInfo = data.getUserInfo();
            Commodity commodity = data.getCommodity();
            if (userInfo == null || commodity == null) {
                return;
            }
            ImageLoaderUtil.loadImage(ivUserPic, userInfo.getUserPic());
            tvUserName.setText(userInfo.getUserName());
            ImageLoaderUtil.loadImage(ivCommodityPic, commodity.getImage());
            tvCommodityMsg.setText(commodity.getMsg());
            tvCommodityMoney.setText(TextUtil.createPrice(commodity.getPrice()));
        }
    }

    public void setListener(ShopcartItemClickListener listener) {
        this.listener = listener;
    }
}