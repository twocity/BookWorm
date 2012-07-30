package com.twocity.bookworm;

import com.twocity.bookworm.service.UpdateIntentService;
import com.twocity.bookworm.utils.BookJsonParser;
import com.twocity.bookworm.utils.Books;
import com.twocity.bookworm.utils.ImageDownloader;
import com.twocity.bookworm.utils.PreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends DashboardActivity implements OnClickListener{
    private final static String TAG = "BookWorm";
    
    private ProgressBar mProgressBar = null;
    private EditText mSearchEdit = null;
    private ImageButton mSearchButton = null;
    private ArrayList<Books> bookList = null;
    private ListView searchList = null;
    private SearchBookAdapter mAdapter = null;
    
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.activity_search);
        
        bookList = new ArrayList<Books>();
        initViews();
    }
    
    private void initViews() {
        mSearchEdit = (EditText) findViewById(R.id.book_search);
        mSearchEdit.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v,int keyCode,KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && 
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        mSearchButton = (ImageButton) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(this);
        
        searchList = (ListView)findViewById(R.id.search_book_list);
        searchList.setOnItemClickListener(myClickListener);
        mProgressBar = (ProgressBar)findViewById(R.id.search_progressbar);
    }
    
    private BroadcastReceiver broadreceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PreferenceUtils.ACTION_SEARCH_BOOK_COMPLETE)){
                String json = intent.getStringExtra(PreferenceUtils.SEARCH_BOOK_JSON);
                bookList = BookJsonParser.Parser(json);
                if(bookList != null){
                    mAdapter = new SearchBookAdapter(SearchActivity.this,bookList);
                    searchList.setAdapter(mAdapter);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(SearchActivity.this, R.string.update_book_failed, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
    
    private void startSearch(){
        Intent intent = new Intent(SearchActivity.this,UpdateIntentService.class);
        intent.setAction(PreferenceUtils.ACTION_SEARCH_BOOK);
        intent.putExtra(PreferenceUtils.SEARCH_ARG, mSearchEdit.getText().toString().trim());
        SearchActivity.this.startService(intent);
        mProgressBar.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                startSearch();
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onStart(){
         super.onStart();
         IntentFilter filter = new IntentFilter();
         filter.addAction(PreferenceUtils.ACTION_SEARCH_BOOK_COMPLETE);
         registerReceiver(broadreceiver,filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(broadreceiver);
    }
    
    final OnItemClickListener myClickListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> a, View view, int position, long id){
            Intent i = new Intent(SearchActivity.this,BookDetail.class);
            Books book = (Books)searchList.getAdapter().getItem(position);
            String url = book.getBookLink();
            i.putExtra(PreferenceUtils.BOOK_DETAIL_LINK, url);
            i.putExtra(PreferenceUtils.BOOK_DETAIL_TITLE, book.getBookTitle());
            startActivity(i);
        }
    };
    
    final private class SearchBookAdapter extends BaseAdapter{
        
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Books> mBookList = new ArrayList<Books>();
        private final ImageDownloader imageDownloader = new ImageDownloader();
        
        public SearchBookAdapter(Context context,ArrayList<Books> books){
            mContext = context;
            mBookList = books;
        }
        
        public int getCount(){
            return mBookList.size();
        }
        
        public Object getItem(int position){
            return mBookList.get(position);
        }
        
        public long getItemId(int position){
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                mInflater = LayoutInflater.from(mContext);  
                convertView = mInflater.inflate(R.layout.bookitem, null);
                
                holder.coverImage = (ImageView)convertView.findViewById(R.id.book_image);
                holder.titleText = (TextView)convertView.findViewById(R.id.book_title);
                holder.authorText = (TextView)convertView.findViewById(R.id.book_author);
      
                convertView.setTag(holder);  
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            
            
            Books item = mBookList.get(position);

            String imagelink = item.getBookImgaeLink();
            imageDownloader.download(imagelink, (ImageView) holder.coverImage);
            holder.titleText.setText(item.getBookTitle());
            holder.authorText.setText(item.getBookAuthor());
            
            return convertView;
        }
        
        private class ViewHolder{
            TextView titleText;
            TextView authorText;
            ImageView coverImage;
        }
    }
}
