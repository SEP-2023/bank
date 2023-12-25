import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QrCodeService } from '../../services/qr-code.service';
import { PaymentRequestDto } from '../../dto/payment-request-dto';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-qr-payment',
  templateUrl: './qr-payment.component.html',
  styleUrls: ['./qr-payment.component.css'],
})
export class QrPaymentComponent implements OnInit {
  id: string = '';
  qr: string = '';

  cardInfo: PaymentRequestDto = new PaymentRequestDto();
  expirationDate: string = '';

  constructor(
    private route: ActivatedRoute,
    private qrService: QrCodeService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.id = params['id'];

      this.qrService.getQrCode(this.id).subscribe((response) => {
        console.log(response.qr);
        this.qr = response.qr;
      });
    });
  }

  pay() {
    this.cardInfo.expirationMonth = this.expirationDate.substring(0, 2);
    this.cardInfo.expirationYear = this.expirationDate.substring(2);
    this.cardInfo.paymentId = this.id;
    this.cardInfo.qr = this.qr;
    console.log(this.cardInfo);
    this.paymentService.pay(this.cardInfo).subscribe({
      next: (response) => {
        window.location.href = response.status;
      },
      error: (err) => {
        console.log(err);
      },
    });
  }
}
