package com.example.seeyou;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarkersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    MakersAdapters makersAdapters;

    public MarkersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarkersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkersFragment newInstance(String param1, String param2) {
        MarkersFragment fragment = new MarkersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_markers, container, false);
        recyclerView = view.findViewById(R.id.RVmarkers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Markers> markersList = new ArrayList<>();

        for (int i = 0; i <20; i++)
        {
            markersList.add(new Markers(i,"CASITA NO. " + i,"San Carlos #" + i,
                    "1244232122, -12421322"
                    ,"Es la colonia donde vivo desde hace años","MARCADOR NO. " + i));
        }

        recyclerView.setAdapter(new MakersAdapters(markersList,getContext()));


        return view;
    }
}