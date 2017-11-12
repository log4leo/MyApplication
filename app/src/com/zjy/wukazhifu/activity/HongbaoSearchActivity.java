package com.zjy.wukazhifu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class HongbaoSearchActivity extends Activity implements OnClickListener {

	private ImageView backImg;

	private PullToRefreshListView listView;
	private HongbaoAdapter adapter;
	private List<Hongbao> dataList = new ArrayList<Hongbao>();

	private int currentPages = 1;
	private int pageSize = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongbaosearch);

		initView();

		getData(currentPages);
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		listView = (PullToRefreshListView) findViewById(R.id.listView);
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
		adapter = new HongbaoAdapter(this);
		listView.setAdapter(adapter);

		TextView emptyTxt = new TextView(this);
		emptyTxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		emptyTxt.setGravity(Gravity.CENTER);
		emptyTxt.setText("暂无数据");
		emptyTxt.setTextSize(20);
		emptyTxt.setVisibility(View.GONE);
		((ViewGroup) listView.getParent()).addView(emptyTxt);
		listView.setEmptyView(emptyTxt);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		default:
			break;
		}
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
						ToastHelper.toast(HongbaoSearchActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(HongbaoSearchActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ToastHelper.toast(HongbaoSearchActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class HongbaoAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private HongbaoAdapter(Context context) {
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
				convertView = mInflater.inflate(R.layout.listitem_hongbaodesc, null);
				holder.typeTxt = (TextView) convertView.findViewById(R.id.typeTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				holder.fromTxt = (TextView) convertView.findViewById(R.id.fromTxt);
				holder.statusTxt = (TextView) convertView.findViewById(R.id.statusTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Hongbao item = dataList.get(position);
			holder.fromTxt.setText(item.getRedDes());
			holder.timeTxt.setText(ToolUtil.formatTime(item.getRedGetTime()));
			holder.amtTxt.setText(ToolUtil.getMoney(item.getRedAchieveAmt(), true));
			holder.typeTxt.setText(getType(item.getRedType()));
			holder.statusTxt.setText(getStatus(item.getRedStatus()));
			return convertView;
		}

		private String getType(String type) {
			String txt = "";
			if ("01".equals(type)) {
				txt = "个人红包";
			} else if ("1002".equals(type)) {
				txt = "群红包";
			}
			return txt;
		}

		private String getFrom(String type) {
			String txt = "";
			if ("1001".equals(type)) {
				txt = "新人注册有礼";
			} else if ("1002".equals(type)) {
				txt = "成功推荐好友";
			}
			return txt;
		}

		private String getStatus(String status) {
			String txt = "";
			if ("00".equals(status)) {
				txt = "未领取";
			} else if ("01".equals(status)) {
				txt = "已领取";
			} else if ("02".equals(status)) {
				txt = "领取失败";
			} else if ("03".equals(status)) {
				txt = "已过期";
			} else if ("04".equals(status)) {
				txt = "已退还";
			}
			return txt;
		}

		class ViewHolder {
			public TextView typeTxt;
			public TextView timeTxt;
			public TextView amtTxt;
			public TextView fromTxt;
			public TextView statusTxt;
		}
	}
}
