

class TrianguloRectangulo:
    def __init__(self, cateto1, cateto2):
        self.cateto1 = cateto1
        self.cateto2 = cateto2

    def area(self):
        # Método puro para calcular el área del triángulo rectángulo
        return 0.5 * self.cateto1 * self.cateto2

    def perimetro(self):
        # Método puro para calcular el perímetro del triángulo rectángulo
        hipotenusa = math.sqrt(self.cateto1**2 + self.cateto2**2)
        return self.cateto1 + self.cateto2 
