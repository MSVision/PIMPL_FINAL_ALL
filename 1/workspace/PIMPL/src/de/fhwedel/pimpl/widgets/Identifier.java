package de.fhwedel.pimpl.widgets;

public interface Identifier<VALUE, KEY> {
   public KEY identify(VALUE obj);
}