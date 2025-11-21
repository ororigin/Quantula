package xyz.ororigin.quantula.researchGUI.fragments;

import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.FrameLayout;
import icyllis.modernui.widget.TextView;

import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TeamManagementFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable DataSet savedInstanceState) {
        var context = requireContext();
        var layout = new FrameLayout(context);
        layout.setBackground(new ColorDrawable(0xFF5F2D5F));

        var title = new TextView(context);
        title.setText("队伍管理界面");
        title.setTextSize(32f);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER);

        var params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        layout.addView(title, params);

        return layout;
    }
}