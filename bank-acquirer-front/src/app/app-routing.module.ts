import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentPageComponent } from './pages/payment-page/payment-page.component';
import { QrPaymentComponent } from './pages/qr-payment/qr-payment.component';

const routes: Routes = [
  { path: 'card/:id', component: PaymentPageComponent },
  { path: 'qr/:id', component: QrPaymentComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
