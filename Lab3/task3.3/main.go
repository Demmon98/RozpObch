package main
import (
	"bytes"
	"fmt"
	 "math/rand"
	"runtime"
	"strconv"
	"time"
)

var r = rand.New(rand.NewSource(55))

func getGID() uint64 {
	b := make([]byte, 64)
	b = b[:runtime.Stack(b, false)]
	b = bytes.TrimPrefix(b, []byte("goroutine "))
	b = b[:bytes.IndexByte(b, ' ')]
	n, _ := strconv.ParseUint(string(b), 10, 64)
	return n
}

func smokerFunc(ingr1 int, ingredients, prod_synchronizer, smoker_synchronizer chan int){

	fmt.Println(getGID()," has a ", ingr1)
	for {
		<-smoker_synchronizer
		var ingr2 int = <-ingredients
		var ingr3 int = <-ingredients
		if (ingr1+ingr2+ingr3) == 6{
			fmt.Println(getGID(), " start smoking")
			time.Sleep(100 * time.Millisecond)
			fmt.Println(getGID()," finish smoking ")
			prod_synchronizer<-1
		} else{
			ingredients<-ingr2
			ingredients<-ingr3
			smoker_synchronizer<-1
		}
		
	}

}


func producerFunc(ingredients, prod_synchronizer, smoker_synchronizer chan int) {
	for {
		<-prod_synchronizer
		ingr1 := rand.Intn(3)+1
		ingr2 := rand.Intn(3)+1
		for ingr2 == ingr1 {
		    ingr2 = rand.Intn(3)+1
		}
		fmt.Println("Producer gives a ",ingr1," and ",ingr2)
		ingredients<-ingr1
		ingredients<-ingr2
		smoker_synchronizer<-1
	}
}
func main(){
	ingredients:=make(chan int,2)
	prod_synchronizer:=make(chan int)
	smoker_synchronizer:=make(chan int)

	go smokerFunc(1, ingredients,prod_synchronizer,smoker_synchronizer)
	go smokerFunc(2, ingredients,prod_synchronizer,smoker_synchronizer)
	go smokerFunc(3, ingredients,prod_synchronizer,smoker_synchronizer)
	go producerFunc(ingredients,prod_synchronizer,smoker_synchronizer)
	prod_synchronizer<-1
	fmt.Scanln();
}
