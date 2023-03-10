package com.irdz.mochameter.ui.ranking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.util.Strings;
import com.irdz.mochameter.R;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.databinding.FragmentRankingBinding;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.service.ReviewService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;
import com.irdz.mochameter.ui.reviewcoffee.ReviewCoffee;
import com.irdz.mochameter.util.CoffeeOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RankingFragment extends Fragment {
    private FragmentRankingBinding binding;
    private int page = 0;

    private String filterText;

    private CoffeeOrder order = CoffeeOrder.GLOBAL_SCORE;
    private boolean reversed = false;

    private boolean listComplete = false;

    public boolean doneLoading = false;

    private boolean isLoadingNextPage = false;
    private boolean newFilter = false;

    public boolean forceLoad = false;

    private String previousOrder, currentOrder;

    public int intresume;

    private FragmentActivity activity;
    private boolean myEvaluations;

    private static Float scanButtonInitialY = null;

    private TextView footerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(binding == null) {
            binding = FragmentRankingBinding.inflate(inflater, container, false);

            intresume = 0;

            createFooterView();

            reverseCheckBoxLogic();

            fillSpinner();

            itemClickedLogic();

            loadMoreOnScroll();

            searchViewLogic();

        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doneLoading = true;
        refreshRanking();
    }

    private void loadMoreOnScroll() {

        binding.lvCoffee.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Check if we've reached the bottom
                int lastItem = firstVisibleItem + visibleItemCount;
                if (!isLoadingNextPage && totalItemCount != 0 && lastItem == totalItemCount) {
                    if(!listComplete) {
                        page++;
                        refreshRanking();
                    }
                }
                moveFloatinButtonWhenOverlaysOnFooterView(totalItemCount, lastItem);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Nothing to do here
            }
        });
    }

    private void moveFloatinButtonWhenOverlaysOnFooterView(final int totalItemCount, final int lastItem) {
        View floattinButton = getActivityAux().findViewById(R.id.btn_scan);
        if(floattinButton != null) {
            if(!myEvaluations && getActivityAux() != null) {
                if (lastItem == totalItemCount) {
                    if(scanButtonInitialY == null) {
                        scanButtonInitialY = floattinButton.getY();
                    }
                    if((floattinButton.getY() + floattinButton.getHeight() >= footerView.getY()
                        && footerView.getY() + floattinButton.getHeight() + footerView.getHeight() >= scanButtonInitialY)) {
                        floattinButton.setY(footerView.getY() + 40);
                    } else {
                        floattinButton.setY(scanButtonInitialY);
                    }
                } else if (floattinButton.getY() != scanButtonInitialY){
                    floattinButton.setY(scanButtonInitialY);
                }
            } else if(scanButtonInitialY != null && floattinButton.getY() != scanButtonInitialY) {
                floattinButton.setY(scanButtonInitialY);
            }
        }
    }

    private void itemClickedLogic() {
        binding.lvCoffee.setOnItemClickListener((parent, view, position, id) -> {
            Review review = (Review) binding.lvCoffee.getItemAtPosition(position);
            if(review != null) {
                Intent intent;
                if(myEvaluations) {
                    intent = new Intent(getActivityAux(), ReviewCoffee.class);
                    intent.putExtra("coffeeDatabase", review.getCoffee());
                    getActivityAux().startActivity(intent);
                } else {
                    intent = new Intent(getActivityAux(), CoffeeDetail.class);
                    intent.putExtra("reviewAvg", review);
                }
                getActivityAux().startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        intresume++;
        if(intresume >= 1) {
            if(listComplete && page > 0) {
                page = 0;
                listComplete = false;
            }
            refreshRanking();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void reverseCheckBoxLogic() {
        binding.cbReverse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reversed = isChecked;
            resetCriteriaToLoad();
            refreshRanking();
        });
    }

    private void fillSpinner() {
        Spinner spinner = binding.spSortyCoffee;

        List<CoffeeOrder> orderList = Arrays.asList(CoffeeOrder.values());
        Collections.sort(orderList, (o1, o2) -> getActivityAux().getString(o1.getResourceText()).compareTo(getActivityAux().getString(o2.getResourceText())));

        Map<String, CoffeeOrder> orderMap = new HashMap<>();
        for (CoffeeOrder order : orderList) {
            orderMap.put(getActivityAux().getString(order.getResourceText()), order);
        }

        List<String> orderTextList = new ArrayList<>();
        for (CoffeeOrder order : orderList) {
            orderTextList.add(getActivityAux().getString(order.getResourceText()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivityAux(), android.R.layout.simple_spinner_item, orderTextList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(orderList.indexOf(CoffeeOrder.GLOBAL_SCORE));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resetCriteriaToLoad();

                previousOrder = currentOrder;
                currentOrder = (String) parent.getItemAtPosition(position);
                order = orderMap.get(currentOrder);
                if(previousOrder != null && currentOrder != previousOrder) {
                    refreshRanking();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void resetCriteriaToLoad() {
        newFilter = true;
        page = 0;
        listComplete = false;
        isLoadingNextPage = false;
    }

    public void refreshRanking() {
        if(newFilter || forceLoad || (doneLoading && !listComplete && !isLoadingNextPage)) {
            isLoadingNextPage = true;
            new QueryTask().execute();
        }
    }
    //degklroplo
    private void showLoading(final int visibility) {
        binding.progressOverlay.setVisibility(visibility);
        binding.progressBar.setVisibility(visibility);
    }

    private class QueryTask extends AsyncTask<Void, Void, List<Review>> {
        @Override
        protected void onPreExecute() {
            // Show the progress bar before the query is executed
            showLoading(View.VISIBLE);
        }

        @Override
        protected List<Review> doInBackground(Void... voids) {
            if(ReviewCoffee.reviewUpdated) {
                resetCriteriaToLoad();
                ReviewCoffee.reviewUpdated = false;
            }
            // Run the database query in the background
            if(myEvaluations) {
                String androidID = UserDaoImpl.getAndroidId(getActivityAux());
                return ReviewService.getInstance().findMyEvaluationsOrderByPaged(filterText, order, reversed, page, androidID);
            } else {
                return ReviewService.getInstance().findAvgOrderByPaged(filterText, order, reversed, page);
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviewsLoaded) {
            RankingListAdapter adapter = null;
            ListView lvCoffee = binding.lvCoffee;

            if(newFilter) {
                lvCoffee.setAdapter(null);
            }

            ListAdapter actualAdapter = lvCoffee.getAdapter();

            if(actualAdapter instanceof RankingListAdapter) {
                adapter = (RankingListAdapter) actualAdapter;
            } else if (actualAdapter instanceof HeaderViewListAdapter) {
                adapter = (RankingListAdapter) ((HeaderViewListAdapter) actualAdapter).getWrappedAdapter();
            }


            List<Review> reviewsToAdd = new ArrayList<>();

            Optional<List<Review>> addedReviewsOpt =
                Optional.ofNullable(adapter)
                .map(RankingListAdapter::getMReviews);

            RankingListAdapter finalAdapter = adapter;
            addedReviewsOpt.ifPresent(addedReviews -> {
                AtomicBoolean objectChanged = new AtomicBoolean(false);
                for (Review newReview : reviewsLoaded) {
                    if(addedReviews.stream().noneMatch(review -> review.getCoffee().getId() == newReview.getCoffee().getId())) {
                        reviewsToAdd.add(newReview);
                    } else {
                        Optional<Review> reviewToUpdate = addedReviews.stream().filter(review -> review.getCoffee().getId() == newReview.getCoffee().getId()).findFirst();
                        reviewToUpdate.ifPresent(rvw -> {
                            int indexToRefresh = addedReviews.indexOf(rvw);
                            if(indexToRefresh != -1) {
                                Review objectToRefresh = addedReviews.get(indexToRefresh);
                                if(objectToRefresh.getCoffee().getId().compareTo(newReview.getCoffee().getId()) == 0 &&
                                    !objectToRefresh.equals(newReview)) {
                                    addedReviews.set(indexToRefresh, newReview);
                                    objectChanged.set(true);
                                }
                            }
                        });
                    }
                }
                if(objectChanged.get()) {
                    addedReviews.sort((o1, o2) -> {
                        switch(order){
                            case GLOBAL_SCORE:
                                return reversed? o1.getScore().compareTo(o2.getScore()) : o2.getScore().compareTo(o1.getScore());
                            case AROMA:
                                return reversed? o1.getAroma().compareTo(o2.getAroma()) : o2.getAroma().compareTo(o1.getAroma());
                            case ACIDITY:
                                return reversed? o1.getAcidity().compareTo(o2.getAcidity()) : o2.getAcidity().compareTo(o1.getAcidity());
                            case BODY:
                                return reversed? o1.getBody().compareTo(o2.getBody()) : o2.getBody().compareTo(o1.getBody());
                            case AFTERTASTE:
                                return reversed? o1.getAftertaste().compareTo(o2.getAftertaste()): o2.getAftertaste().compareTo(o1.getAftertaste());
                            default:
                                return 0;
                        }
                    });
                }
                int sizeBefore = addedReviews.size();
                getActivityAux().runOnUiThread(() -> {
                    addedReviews.addAll(reviewsToAdd);
                    Optional.ofNullable(finalAdapter).ifPresent(aa -> aa.notifyDataSetChanged());
                });
                if(addedReviews.size() == sizeBefore && page > 0) {
                    listComplete = true;
                }
            });


            // Update the UI with the query results
            if(adapter == null) {
                getActivityAux().runOnUiThread(() -> {
                    RankingListAdapter adptr = new RankingListAdapter(getActivityAux(), reviewsLoaded);
                    lvCoffee.setAdapter(adptr);
                    adptr.notifyDataSetChanged();
                });
            }
            isLoadingNextPage = false;
            newFilter = false;
            forceLoad = false;

            showLoading(View.INVISIBLE);

        }
    }

    private void searchViewLogic() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if(!Strings.isEmptyOrWhitespace(query)) {
                    filterText = query;
                    page = 0;
                    newFilter = true;
                    refreshRanking();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(TextUtils.isEmpty(newText) && filterText != null) {
                    filterText = null;
                    page = 0;
                    newFilter = true;
                    refreshRanking();
                }
                return false;
            }
        });
    }

    private void createFooterView() {
        if(!myEvaluations) {
            footerView = new TextView(getActivityAux());

            footerView.setText(R.string.footer_ranking);

            footerView.setGravity(Gravity.CENTER);

            footerView.setPadding(20, 20, 20, 20);

            footerView.setMaxHeight(200);

            binding.lvCoffee.addFooterView(footerView);
        }
    }

    public void setActivity(final FragmentActivity activity) {
        this.activity = activity;
    }

    public FragmentActivity getActivityAux() {
        return Optional.ofNullable(super.getActivity()).orElse(this.activity);
    }

    public void setMyEvalutions(final boolean b) {
        this.myEvaluations = b;
    }
}