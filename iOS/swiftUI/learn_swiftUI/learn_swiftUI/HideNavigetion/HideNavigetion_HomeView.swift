//
//  HideNavigetion_HomeView.swift
//  learn_swiftUI
//
//  Created by 성준모 on 5/16/24.
//

import SwiftUI

struct HideNavigetion_HomeView: View {
    
    @State var isShowingNavigation = true
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.secondary.opacity(0.2)
                
                VStack {
                    Image(systemName: "globe")
                        .imageScale(.large)
                        .foregroundStyle(.tint)
                    Text("Hello world")
                    
                    Button("Toggle Navigaion") {
                        isShowingNavigation.toggle()
                    }
                    .padding()
                    .buttonStyle(.borderedProminent)
                } //: VSTACK
                .padding()
                .navigationTitle("Home")
                
                .toolbar(
                    isShowingNavigation ? .visible : .hidden,
                    for: .tabBar)
                
            } //: ZSTACK
        } //: NAVIGATIONSTACK
    }
}

#Preview {
    HideNavigetion_HomeView()
}
