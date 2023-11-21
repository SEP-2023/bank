import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './components/navbar/navbar.component';
import { CardFormComponent } from './components/card-form/card-form.component';
import { PaymentPageComponent } from './pages/payment-page/payment-page.component';
import {FormsModule} from "@angular/forms";
import { NumericDirective } from './components/directives/numeric.directive';
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    CardFormComponent,
    PaymentPageComponent,
    NumericDirective,
    NumericDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
