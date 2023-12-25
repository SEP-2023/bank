import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { PaymentPageComponent } from './pages/payment-page/payment-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CardFormComponent } from './components/card-form/card-form.component';
import { NumericDirective } from './components/directives/numeric.directive';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { QrPaymentComponent } from './pages/qr-payment/qr-payment.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    CardFormComponent,
    PaymentPageComponent,
    NumericDirective,
    QrPaymentComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
