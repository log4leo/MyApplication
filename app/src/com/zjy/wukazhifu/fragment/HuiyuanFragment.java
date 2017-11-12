package com.zjy.wukazhifu.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Huiyuan;
import com.zjy.wukazhifu.util.ToolUtil;

public class HuiyuanFragment extends Fragment {
	private View view;
	private ListView listView;
	private HuiyuanAdapter adapter;
	private List<Huiyuan> dataList = new ArrayList<Huiyuan>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_huiyuan, container, false);
		dataList = (List<Huiyuan>) getArguments().getSerializable("data");
		initView();
		return view;
	}

	private void initView() {
		listView = (ListView) view.findViewById(R.id.listView);
		adapter = new HuiyuanAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	public class HuiyuanAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private HuiyuanAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_huiyuan, null);
				holder.classImg = (ImageView) convertView.findViewById(R.id.classImg);
				holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
				holder.accountTxt = (TextView) convertView.findViewById(R.id.accountTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.statusImg = (ImageView) convertView.findViewById(R.id.statusImg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Huiyuan data = dataList.get(position);
			String merclass = data.getMerclass();
			if ("40".equals(merclass)) {
				holder.classImg.setImageResource(R.drawable.huanjin);
			} else if ("50".equals(merclass)) {
				holder.classImg.setImageResource(R.drawable.huanjin);
			} else if ("60".equals(merclass)) {
				holder.classImg.setImageResource(R.drawable.huanjin);
			}
			holder.nameTxt.setText(data.getCustName());
			holder.accountTxt.setText(data.getCustLogin());
			holder.timeTxt.setText(ToolUtil.formatTime(data.getCustRegDatetime()));
			String status = data.getCustStatus();
			if ("0".equals(status)) {
				holder.statusImg.setImageResource(R.drawable.weiwanshan);
			} else if ("1".equals(status)) {
				holder.statusImg.setImageResource(R.drawable.shenhezhong);
			} else if ("2".equals(status)) {
				holder.statusImg.setImageResource(R.drawable.yirenzheng);
			}else if ("3".equals(status)) {
				holder.statusImg.setImageResource(R.drawable.weitongguo);
			}
			return convertView;
		}

		class ViewHolder {
			public ImageView classImg;
			public TextView nameTxt;
			public TextView accountTxt;
			public TextView timeTxt;
			public ImageView statusImg;
		}
	}

}
