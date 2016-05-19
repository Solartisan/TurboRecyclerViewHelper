package cc.solart.turbo.simple.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import cc.solart.turbo.BaseTurboAdapter;
import cc.solart.turbo.BaseViewHolder;
import cc.solart.turbo.simple.R;
import cc.solart.turbo.simple.model.MultiModel;

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public class MultiTypeAdapter extends BaseTurboAdapter<MultiModel,MultiTypeAdapter.MultiViewHolder>{

    private static final int TYPE_1 = 0;
    private static final int TYPE_2 = 1;
    private static final int TYPE_3 = 2;

    public MultiTypeAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getDefItemViewType(int position) {
        MultiModel model = getItem(position);
        return model.getmType();
//        if(position%3==0){
//            return TYPE_1;
//        }else if(position%3==1){
//            return TYPE_2;
//        }else{
//            return TYPE_3;
//        }
    }

    @Override
    protected MultiViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_1){
            return new Type1Holder(inflateItemView(R.layout.item_type_1,parent));
        }else if(viewType == TYPE_2){
            return new Type1Holder(inflateItemView(R.layout.item_type_2,parent));
        }else{
            return new Type1Holder(inflateItemView(R.layout.item_type_3,parent));
        }
    }

    @Override
    protected void convert(MultiViewHolder holder, MultiModel item) {

    }

    class MultiViewHolder extends BaseViewHolder{

        public MultiViewHolder(View view) {
            super(view);
        }
    }

    class Type1Holder extends MultiViewHolder{

        public Type1Holder(View view) {
            super(view);
        }
    }
}
