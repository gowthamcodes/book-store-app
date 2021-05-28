package com.gowtham.bookstore;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String Pages = " pages";

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false
            );
        }

        Book currentBook = getItem(position);
        TextView titleTextView = listItemView.findViewById(R.id.title);
        ImageView thumbnailImageView = listItemView.findViewById(R.id.thumbnail);
        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = listItemView.findViewById(R.id.author);
        authorTextView.setText(currentBook.getAuthor());

        TextView pageTextView = listItemView.findViewById(R.id.pages);
        String pageCount = currentBook.getPages();
        if (pageCount != "N/A") {
            pageCount = pageCount + Pages;
        }
        pageTextView.setText(pageCount);

        TextView yearTextView = listItemView.findViewById(R.id.year);
        yearTextView.setText(formatYear(currentBook.getYear()));

        TextView pdfTextView = listItemView.findViewById(R.id.pdf);
        TextView ePubTextView = listItemView.findViewById(R.id.epub);

        String thumbnail = currentBook.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.get().load(thumbnail).into(thumbnailImageView);
        } else {
            thumbnailImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.no_preview_available));
        }

        int availabilityColorResourceId = R.color.textColorNotAvailable;
        if (!currentBook.isPdf()) {
            pdfTextView.setTextColor(ContextCompat.getColor(getContext(), availabilityColorResourceId));
        }
        if (!currentBook.isEpub()) {
            ePubTextView.setTextColor(ContextCompat.getColor(getContext(), availabilityColorResourceId));
        }

        return listItemView;
    }

    private String formatYear(String year) {
        if (TextUtils.isEmpty(year)) {
            return null;
        }
        if (year.contains("-")) {
            return year.split("-", 4)[0];
        }
        return year;
    }
}
