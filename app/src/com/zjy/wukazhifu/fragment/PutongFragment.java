package com.zjy.wukazhifu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.zjy.wukazhifu.entity.Shouyi;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class PutongFragment extends Fragment {
	private View view;

	private PullToRefreshListView listView;
	private ShouyiAdapter adapter;
	private List<Shouyi> dataList = new ArrayList<Shouyi>();

	private int currentPages = 1;
	private int pageSize = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_putong, container, false);
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
		String url = Constants.ADDR_shouyi;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("busType", "01");
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
						JSONArray profitInfoList = REP_BODY.optJSONArray("profitInfoList");
						Gson gson = new Gson();
						List<Shouyi> list = gson.fromJson(profitInfoList.toString(), new TypeToken<List<Shouyi>>() {
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
				convertView = mInflater.inflate(R.layout.listitem_shouyi, null);
				holder.levelTxt = (TextView) convertView.findViewById(R.id.levelTxt);
				holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				holder.shouyiTxt = (TextView) convertView.findViewById(R.id.shouyiTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Shouyi item = dataList.get(position);
			holder.levelTxt.setText(item.getLevelName());
			holder.nameTxt.setText(item.getCustName().replaceAll("(?<=.).", "*"));
			holder.timeTxt.setText(ToolUtil.formatTime(item.getOrdTime()));
			holder.shouyiTxt.setText(ToolUtil.getMoney(item.getSharAmt(), true));
			holder.amtTxt.setText(ToolUtil.getMoney(item.getOrderAmt(), false));
			return convertView;
		}

		class ViewHolder {
			public TextView levelTxt;
			public TextView nameTxt;
			public TextView timeTxt;
			public TextView amtTxt;
			public TextView shouyiTxt;
		}
	}

}
