/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.herocode.ecmat.interfaces;

/**
 *
 * @author Victor Hugo <victor.hugo.origins@gmail.com>
 */
public interface CheckoutCreator {
    
    /**
     * Build a checkout in the PagSeguro system and return the payment URL
     * @return Payment URL for this checkout
     */
    String buildCheckout(String referenceCode);
    
    /**
     * Build a checkout in the PagSeguro system and return the payment URL
     * @return Payment URL for this checkout
     */
    default String buildCheckout(int referenceCode){
        
        return buildCheckout(String.valueOf(referenceCode));
    }
    
}
