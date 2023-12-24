//
//  ContentView.swift
//  iOS_Nested_X
//
//  Created by Noah Boyers on 12/24/23.
//

import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        Text(Greeting().greet())
        .padding()
    }
}

#Preview {
    ContentView()
}
