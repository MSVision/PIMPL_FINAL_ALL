package de.fhwedel.pimpl.widgets;

import de.fhwedel.pimpl.model.BusinessObject;

public class BOSelectListBox<VALUE extends BusinessObject<KEY>, KEY> extends EditableListBox<VALUE, KEY> {
   
   public BOSelectListBox() {
      this.setPrinter(new Printer<VALUE>() {
         @Override
         public String print(VALUE obj) {
            return obj != null ? obj.getCaption() : "";
         }         
      });
      this.setIdentifier(new Identifier<VALUE, KEY>() {
         @Override
         public KEY identify(VALUE obj) { 
            return obj != null ? obj.getID() : null;
         }         
      });
   }

}
