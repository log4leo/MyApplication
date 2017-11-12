package com.zjy.wukazhifu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Msg;
import com.zjy.wukazhifu.entity.Order;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;
import com.zjy.wukazhifu.view.LoadingDialog;

public class MsgFragment extends Fragment {
	private View view;

	private ListView listView;
	private MsgAdapter adapter;
	private List<Msg> dataList = new ArrayList<Msg>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_msg, container, false);
		initView();
		msg();
		return view;
	}

	private void initView() {
		listView = (ListView) view.findViewById(R.id.listView);
		adapter = new MsgAdapter(getActivity());
		listView.setAdapter(adapter);

		TextView emptyTxt = new TextView(getActivity());
		emptyTxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		emptyTxt.setGravity(Gravity.CENTER);
		emptyTxt.setText("暂无数据");
		emptyTxt.setTextSize(20);
		emptyTxt.setVisibility(View.GONE);
		((ViewGroup) listView.getParent()).addView(emptyTxt);
		listView.setEmptyView(emptyTxt);
	}

	public class MsgAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private MsgAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.listitem_msg, null);
				holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.titleLayout);
				holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
				holder.titleTxt = (TextView) convertView.findViewById(R.id.titleTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.contentTxt = (TextView) convertView.findViewById(R.id.contentTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Msg item = dataList.get(position);
			holder.timeTxt.setText(ToolUtil.formatTime(item.getNoticeIssueDate()));
			holder.titleTxt.setText(item.getNoticeTitle());
			holder.contentTxt.setText(item.getNoticeBody());
			holder.contentLayout.setVisibility(View.GONE);
			final View body = holder.contentLayout;
			holder.titleLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (body.getVisibility() == view.GONE) {
						body.setVisibility(View.VISIBLE);
					} else {
						body.setVisibility(View.GONE);
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			public LinearLayout titleLayout;
			public TextView titleTxt;
			public TextView timeTxt;
			public LinearLayout contentLayout;
			public TextView contentTxt;
		}
	}

	public void msg() {
		LoadingDialog.show(getActivity());
		String url = Constants.ADDR_msg;
		String cookie = PreferenceHelper.getCookie();
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				LoadingDialog.dismiss();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray tranList = REP_BODY.optJSONArray("noticeList");
						Gson gson = new Gson();
						List<Msg> list = gson.fromJson(tranList.toString(), new TypeToken<List<Msg>>() {
						}.getType());
						dataList.clear();
						dataList.addAll(list);
						adapter.notifyDataSetChanged();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(), RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(), "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LoadingDialog.dismiss();
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}
}
