package com.irdz.mochameter.ui.ranking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.irdz.mochameter.R;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.service.CoffeeService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RankingListAdapter extends BaseAdapter {

    private Context mContext;

    @Getter
    private List<Review> mReviews;

    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public Review getItem(int position) {
        return mReviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.fragment_ranking_list, parent, false);
        }

        Review review = mReviews.get(position);

        View finalView = view;
        Optional.ofNullable(review.getCoffee()).ifPresent(cof -> {
            Coffee coffee = cof;
            if(cof.getCoffeeName() == null) {
                coffee = CoffeeService.getInstance().findById(cof.getId());
            }
            review.setCoffee(coffee);

            ImageView imageView = finalView.findViewById(R.id.ivCoffeeRanking);
            Optional.ofNullable(coffee.getImageUrl()).map(Picasso.get()::load)
                .ifPresent(requestCreator -> requestCreator.into(imageView));

            TextView tvName = finalView.findViewById(R.id.tvName);
            tvName.setText(coffee.getCoffeeName());
            TextView tvBrand = finalView.findViewById(R.id.tvBrand);
            tvBrand.setText(coffee.getBrand());
        });


        RatingBar rbAcidity = view.findViewById(R.id.rbAcidity);
        RatingBar rbAroma = view.findViewById(R.id.rbAroma);
        RatingBar rbBody = view.findViewById(R.id.rbBody);
        RatingBar rbAftertaste = view.findViewById(R.id.rbAftertaste);
        RatingBar rbScore = view.findViewById(R.id.rbScore);

        rbAcidity.setRating(review.getAcidity().floatValue());
        rbAroma.setRating(review.getAroma().floatValue());
        rbBody.setRating(review.getBody().floatValue());
        rbAftertaste.setRating(review.getAftertaste().floatValue());
        rbScore.setRating(review.getScore().floatValue());

        return view;
    }
}