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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Hongbao;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class ShengjiFragment extends Fragment {
	private View view;

	private TextView allTxt;
	private PullToRefreshListView listView;
	private ShouyiAdapter adapter;
	private List<Hongbao> dataList = new ArrayList<Hongbao>();

	private int currentPages = 1;
	private int pageSize = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shengji, container, false);
		initView();
		getData(currentPages);
		return view;
	}

	private void initView() {
		allTxt = (TextView) view.findViewById(R.id.allTxt);
		listView = (PullToRefreshListView) view.findViewById(R.id.listView);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				currentPages = 1;
				getData(currentPages);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				currentPages++;
				getData(currentPages);
			}
		});
		adapter = new ShouyiAdapter(getActivity());
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

	private void getData(int page) {
		String url = Constants.ADDR_chahongbao;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("pageSize", "" + pageSize);
		param.put("currentPages", "" + page);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {

						JSONObject redInfo = REP_BODY.optJSONObject("redInfo");
						allTxt.setText("￥" + ToolUtil.getMoney(redInfo.optString("sum0"), false));

						JSONArray profitInfoList = REP_BODY.optJSONArray("redPacketList");
						Gson gson = new Gson();
						List<Hongbao> list = gson.fromJson(profitInfoList.toString(), new TypeToken<List<Hongbao>>() {
						}.getType());
						if (currentPages == 1) {
							dataList.clear();
						}
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
				listView.onRefreshComplete();
				ToastHelper.toast(getActivity(), "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class ShouyiAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private ShouyiAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.listitem_hongbao, null);
				holder.typeTxt = (TextView) convertView.findViewById(R.id.typeTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Hongbao item = dataList.get(position);
			holder.typeTxt.setText(item.getRedDes());
			holder.timeTxt.setText(ToolUtil.formatTime(item.getRedGetTime()));
			holder.amtTxt.setText(ToolUtil.getMoney(item.getRedAchieveAmt(), true));
			return convertView;
		}

		private String getType(String type) {
			String txt = "";
			if ("1001".equals(type)) {
				txt = "新人注册有礼";
			} else if ("1002".equals(type)) {
				txt = "成功推荐好友";
			}
			return txt;
		}

		class ViewHolder {
			public TextView typeTxt;
			public TextView timeTxt;
			public TextView amtTxt;
		}
	}

}
