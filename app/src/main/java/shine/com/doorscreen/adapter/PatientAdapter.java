package shine.com.doorscreen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import shine.com.doorscreen.R;
import shine.com.doorscreen.entity.PatientInfo;
import shine.com.doorscreen.fragment.DoorFragment;
import shine.com.doorscreen.util.LogUtil;

/**
 * Created by Administrator on 2016/8/16.
 * 病人情况适配器
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientHolder> {
    private static final String TAG = "DripAdapter";
    private List<PatientInfo.Patient> mPatientList;
    private DoorFragment mContext;

    public PatientAdapter(DoorFragment context) {
        mContext = context;
        mPatientList = new ArrayList<>();
    }

    public void onDateChange(List<PatientInfo.Patient> patientList) {
        if (patientList != null) {
            mPatientList.clear();
            mPatientList.addAll(patientList);
            notifyDataSetChanged();
        }
    }


    public void onPatientCall(List<String> names) {
        if (names == null || names.size() == 0) {
            for (PatientInfo.Patient patient : mPatientList) {
                patient.setCalling(false);
            }
            notifyDataSetChanged();
            return;
        }
        LogUtil.d(TAG, "names:" + names);
        for (int i = 0; i < mPatientList.size(); i++) {
            PatientInfo.Patient patient = mPatientList.get(i);
            if (names.contains(patient.getBedno())) {
                patient.setCalling(true);
                mContext.moveToCallingPatient(i);
            }else{
                patient.setCalling(false);
            }
        }
        LogUtil.d(TAG, "mPatientList:" + mPatientList);
        notifyDataSetChanged();
    }



    @Override
    public PatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paient_info, parent, false);
        return new PatientHolder(view, mContext.getActivity());
    }

    @Override
    public void onBindViewHolder(PatientHolder holder, int position) {
        PatientInfo.Patient patient = mPatientList.get(position);
        holder.bind(patient);
    }

    @Override
    public int getItemCount() {
        return mPatientList.size();
    }

    static class PatientHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_bedroom_number)
        TextView mTvBedroomNumber;
        @Bind(R.id.tv_patient_detail)
        TextView mTvPatientDetail;
        private Context context;

        private PatientHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }

        public void bind(PatientInfo.Patient patient) {
            Log.d(TAG, patient.toString());
            /*if (patient.getBedno().endsWith("床")) {
                mTvBedroomNumber.setText(patient.getBedno().replace("床", "\n床"));
            } else {
                mTvBedroomNumber.setText(String.format(Locale.CHINA, "%s\n床", patient.getBedno()));
            }*/
            //床号长度超过4，比如233+床就换行显示床
            if (patient.getBedno().length() > 4) {
                mTvBedroomNumber.setText(patient.getBedno().replace("床", "\n床"));
            }else{
                mTvBedroomNumber.setText(patient.getBedno());
            }
            if (patient.isCalling()) {
                mTvPatientDetail.setText("正在呼叫");
                mTvPatientDetail.setTextColor(context.getResources().getColor(R.color.color_orange));
            } else {
                mTvPatientDetail.setText(patient.getPatientname());
                mTvPatientDetail.setTextColor(context.getResources().getColor(R.color.color_background_main));
            }
        }

    }


}
