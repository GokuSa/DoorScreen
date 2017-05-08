package shine.com.doorscreen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.entity.Person;
import shine.com.doorscreen.fragment.DoorFragment;
import shine.com.doorscreen.util.LogUtil;

/**
 * Created by Administrator on 2016/8/11.
 * 医生信息适配器
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorHolder> {
    private static final String TAG = "DoctorAdapter";
    private DoorFragment mDoorFragment;
    private List<Person> mPersonList=new ArrayList<>();

    public DoctorAdapter(DoorFragment context) {
        mDoorFragment = context;
    }

    public void onDataChange(List<Person> personList) {
        mPersonList.clear();
        mPersonList.addAll(personList);
        LogUtil.d(TAG, "ondatechange "+personList.toString());
        notifyDataSetChanged();
    }



    @Override
    public DoctorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorHolder(view);
    }

    @Override
    public void onBindViewHolder(DoctorHolder holder, int position) {
            holder.bind(mPersonList.get(position),mDoorFragment);
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

    static class DoctorHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_doctor_title)
        TextView mTvDoctorTitle;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.iv_avatar)
        ImageView mImageViewAvatar;

         DoctorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Person person,DoorFragment doorFragment) {
            mTvDoctorTitle.setText(person.getTitle());
            LogUtil.d(TAG, person.getImg());
//            System.setProperty("http.keepAlive", "false");
            Glide.with(doorFragment).load(person.getImg())
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                    .into(mImageViewAvatar);
            mTvName.setText(person.getName());
        }
    }


}
