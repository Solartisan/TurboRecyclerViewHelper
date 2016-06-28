package cc.solart.turbo.simple.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cc.solart.turbo.BaseTurboAdapter;
import cc.solart.turbo.BaseViewHolder;
import cc.solart.turbo.simple.R;
import cc.solart.turbo.simple.model.MultiModel;

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public class MultiTypeAdapter extends BaseTurboAdapter<MultiModel,BaseViewHolder>{

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
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_1){
            return new Type1Holder(inflateItemView(R.layout.item_type_1,parent));
        }else if(viewType == TYPE_2){
            return new Type2Holder(inflateItemView(R.layout.item_type_2,parent));
        }else{
            return new Type3Holder(inflateItemView(R.layout.item_type_3,parent));
        }
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiModel item) {
        // TODO Render UI
    }


    class Type1Holder extends BaseViewHolder{
        ImageView image;
        public Type1Holder(View view) {
            super(view);
            image = findViewById(R.id.image);
        }
    }

    class Type2Holder extends BaseViewHolder{
        ImageView image;
        public Type2Holder(View view) {
            super(view);
            image = findViewById(R.id.image);
        }
    }

    class Type3Holder extends BaseViewHolder{
        TextView textView;
        public Type3Holder(View view) {
            super(view);
            textView = findViewById(R.id.text);
        }
    }
}
