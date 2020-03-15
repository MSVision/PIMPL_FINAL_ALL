package de.fhwedel.pimpl.widgets;

import java.util.Arrays;

public class EnumSelectListBox<VALUE extends Enum<VALUE>> extends EditableListBox<VALUE, Integer> {
   
   public EnumSelectListBox() {
      this.setPrinter(new Printer<VALUE>() {
         @Override
         public String print(VALUE obj) {
            return obj == null ? null : obj.toString();
         }         
      });
      this.setIdentifier(new Identifier<VALUE, Integer>() {
         @Override
         public Integer identify(VALUE obj) { 
            return obj == null ? null : obj.ordinal();
         }         
      });
   }
   
   public EnumSelectListBox(Class<VALUE> enum_type) {
      this();
      this.setEnum(enum_type);
   }

   public EnumSelectListBox<VALUE> setEnum(Class<VALUE> enum_type) {
      VALUE[] enum_values = enum_type.getEnumConstants();
      this.setAcceptableValues( Arrays.asList(enum_values) );
      return this;
   }

}
