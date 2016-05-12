package picklenostra.user_app.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import picklenostra.user_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawalFragment extends Fragment {


    public WithdrawalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.withdrawal_fragment, container, false);
    }

}
