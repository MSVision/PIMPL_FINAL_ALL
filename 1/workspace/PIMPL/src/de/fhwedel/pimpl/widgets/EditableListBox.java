package de.fhwedel.pimpl.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.ListBox;

public class EditableListBox<VALUE, KEY> extends ListBox implements LeafValueEditor<VALUE> {
   
   private Printer<VALUE> p;
   private Identifier<VALUE, KEY> id;
   private Map<Object, Integer> obj_to_idx = new HashMap<Object, Integer>();
   private Map<Integer, VALUE> idx_to_obj = new HashMap<Integer, VALUE>();
   
   public EditableListBox() {
      super();
      this.setVisibleItemCount(5);
      this.setPrinter(new Printer<VALUE>() {
         @Override
         public String print(VALUE obj) {
            return obj != null ? obj.toString() : null;
         }         
      });
      this.setIdentifier(new Identifier<VALUE, KEY>() {
         @Override
         public KEY identify(VALUE obj) { 
            return null;
         }         
      });
   }
   
   public EditableListBox<VALUE, KEY> setPrinter(Printer<VALUE> p) {
      this.p = p;
      return this;
   }
   
   public EditableListBox<VALUE, KEY> setIdentifier(Identifier<VALUE, KEY> id) {
      this.id = id;
      return this;
   }
   
   @Override
   public void setValue(VALUE value) {
      Object id = this.id.identify(value);
      if(value != null && this.obj_to_idx.containsKey(id) ) {
         this.setSelectedIndex( this.obj_to_idx.get(id) );
      } else
         this.setSelectedIndex(-1);
   }

   @Override
   public VALUE getValue() {
      if(this.idx_to_obj.containsKey(this.getSelectedIndex())) {
         return this.idx_to_obj.get( this.getSelectedIndex() );
      } else {     
         return null;
      }
   }
   
   public void setAcceptableValues(List<VALUE> values) {      
      this.obj_to_idx.clear();
      this.idx_to_obj.clear();
      this.clear();
      int idx = 0;
      for(VALUE elem : values) {
         this.addItem( this.p.print(elem) );
         this.obj_to_idx.put(this.id.identify(elem), idx);
         this.idx_to_obj.put(idx++, elem);
      }
   }

}

