package com.home.skiffdro.common;

import androidx.recyclerview.widget.RecyclerView;

public final class Utils {
    public static String ValToPrint(double val) {
        if (val == 0) val = 0; //Ээээ... надо ))) Ну типа, избавление от -0 =)
        return String.format("%.2f", val);
    }

    public static void SetRWPosition(RecyclerView recyclerView, int Row)
    {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(Row);
        lm.startSmoothScroll(smoothScroller);
    }
}
