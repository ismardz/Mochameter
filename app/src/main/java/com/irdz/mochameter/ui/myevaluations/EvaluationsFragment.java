package com.irdz.mochameter.ui.myevaluations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.irdz.mochameter.databinding.FragmentDashboardBinding;
import com.irdz.mochameter.ui.ranking.RankingFragment;

public class EvaluationsFragment extends Fragment {

    RankingFragment rankingFragment;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rankingFragment = new RankingFragment();
        rankingFragment.setActivity(getActivity());
        rankingFragment.setMyEvalutions(true);

//        binding = FragmentDashboardBinding.inflate(inflater, container, false);
//        binding.getRoot().addView(rankingFragment.onCreateView(inflater, container, savedInstanceState));

        return rankingFragment.onCreateView(inflater, container, savedInstanceState);
    }



    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rankingFragment.doneLoading = true;
        rankingFragment.refreshRanking();
    }

    @Override
    public void onResume() {
        super.onResume();
        rankingFragment.forceLoad = true;
        rankingFragment.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}