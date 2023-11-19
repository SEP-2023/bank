import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PaymentPageComponent} from "./pages/payment-page/payment-page.component";

const routes: Routes = [{path:'',component:PaymentPageComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
