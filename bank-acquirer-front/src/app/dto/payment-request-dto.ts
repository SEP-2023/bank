export class PaymentRequestDto{
  constructor(
    public pan:string='',
    public securityCode:string='',
    public cardholderName:string='',
    public expirationMonth:string='',
    public expirationYear:string='',
    public paymentId:string='',
  ) {}
}
