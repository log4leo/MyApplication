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
import com.zjy.wukazhifu.entity.Order;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class ShoukuantixianFragment extends Fragment {
	private View view;

	private PullToRefreshListView listView;
	private OrderAdapter adapter;
	private List<Order> dataList = new ArrayList<Order>();

	private int currentPages = 1;
	private int pageSize = 10;
	private String busType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shoukuantixian, container, false);
		busType = getArguments().getString("busType");
		initView();
		getData(currentPages);
		return view;
	}

	private void initView() {
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
		adapter = new OrderAdapter(getActivity());
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
		String url = Constants.ADDR_order;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("busType", busType);
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
						JSONArray tranList = REP_BODY.optJSONArray("tranList");
						Gson gson = new Gson();
						List<Order> list = gson.fromJson(tranList.toString(), new TypeToken<List<Order>>() {
						}.getType());
						if(currentPages==1){
							dataList.clear();
						}
						dataList.addAll(list);
						adapter.notifyDataSetChanged();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(getActivity(),RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(getActivity(),"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ToastHelper.toast(getActivity(),"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class OrderAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private OrderAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.listitem_order, null);
				holder.typeTxt = (TextView) convertView.findViewById(R.id.typeTxt);
				holder.statusTxt = (TextView) convertView.findViewById(R.id.statusTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.noTxt = (TextView) convertView.findViewById(R.id.noTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order item = dataList.get(position);
			holder.typeTxt.setText(getType(item.getPAYTYPE()));
			if ("03".equals(busType)) {
				holder.typeTxt.setText("提现T+0");
			}
			holder.statusTxt.setText(getStatus(item.getOrdstatus()));
			holder.timeTxt.setText(ToolUtil.formatTime(item.getOrdtime()));
			holder.noTxt.setText(item.getPrdordno());
			holder.amtTxt.setText(ToolUtil.getMoney(item.getOrdamt(), true));
			return convertView;
		}

		private String getType(String code) {
			String type = "";
			if ("03".equals(code)) {
				type = "快捷支付";
			} else if ("04".equals(code)) {
				type = "微信支付";
			} else if ("05".equals(code)) {
				type = "支付宝";
			}
			return type;
		}

		private String getStatus(String code) {
			// 00||06 未处理。01 成功。02 失败。03||04||05||07||08 处理中。
			String status = "";
			if ("00".equals(code) || "06".equals(code)) {
				status = "未处理";
			} else if ("01".equals(code)) {
				status = "成功";
			} else if ("02".equals(code)) {
				status = " 失败";
			} else if ("03".equals(code) || "04".equals(code) || "05".equals(code) || "07".equals(code) || "08".equals(code)) {
				status = " 处理中";
			}
			return status;
		}

		class ViewHolder {
			public TextView typeTxt;
			public TextView statusTxt;
			public TextView timeTxt;
			public TextView noTxt;
			public TextView amtTxt;
		}
	}

}
